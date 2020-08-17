package com.suchengyu.gmall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suchengyu.gmall.cart.mapper.CartInfoMapper;
import com.suchengyu.gmall.cart.service.CartAplService;
import com.suchengyu.gmall.common.constant.RedisConst;
import com.suchengyu.gmall.model.cart.CartInfo;
import com.suchengyu.gmall.model.product.SkuInfo;
import com.suchengyu.gmall.product.controller.ProductFeignClient;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * CartAplServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-06
 * @Description:
 */
@Service
public class CartAplServiceImpl implements CartAplService {
    @Autowired
    private CartInfoMapper cartInfoMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;

    //根据skuId删除购物车
    public void deleteCart(String userId, Long skuId) {
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id", skuId);
        wrapper.eq("user_id", userId);
        cartInfoMapper.delete(wrapper);
        //删除缓存对象
        String cartKey = this.getCartKey(userId);
        redisTemplate.boundHashOps( cartKey).delete(skuId.toString());
    }

    //更改购物车选择状态
    public void checkCart(String userId, Integer isChecked, Long skuId) {
        //第一个参数表示修改的数据,第二个参数表示条件
        //更新数据库
        CartInfo cartInfo = new CartInfo();
        cartInfo.setIsChecked(isChecked);
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("sku_id", skuId);
        cartInfoMapper.update(cartInfo, wrapper);
        //更新缓存
        String cartKey = this.getCartKey(userId);
        CartInfo cartInfoUpd   = (CartInfo) redisTemplate.boundHashOps(cartKey).get(skuId.toString());//先查
        cartInfoUpd.setIsChecked(isChecked);
        redisTemplate.boundHashOps(cartKey).put(skuId.toString(), cartInfoUpd);//更新缓存
        this.setCartKeyExpire(cartKey);//设置过期时间
    }

    //合并正式购物车
    public boolean mergeToCartList(String userId, String userTempId) {
       //正式购物车数据
        QueryWrapper<CartInfo> userIdWrapper = new QueryWrapper<>();
        userIdWrapper.eq("user_id", userId);
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(userIdWrapper);

        //临时购物车数据
        QueryWrapper<CartInfo> userTempIdWrapper = new QueryWrapper<>();
        userTempIdWrapper.eq("user_id", userTempId);
        List<CartInfo> cartInfosTempUser = cartInfoMapper.selectList(userTempIdWrapper);
        // 临时购物车有值,正式购物车无值,添加到正式购物车
        if (null == cartInfoList || cartInfoList.size() == 0){
            if (!CollectionUtils.isEmpty(cartInfosTempUser)){
                for (CartInfo cartInfo : cartInfosTempUser) {
                    cartInfo.setId(null);
                    cartInfo.setUserId(userId);
                    cartInfoMapper.insert(cartInfo);
                }
            }
        }else {
            //正式购物车有值,给外层循环起别名
            cartFor : for (CartInfo cartInfoTemp : cartInfosTempUser) {
                Long skuIdTemp = cartInfoTemp.getSkuId();
                for (CartInfo cartInfo : cartInfoList) {
                    Long skuId = cartInfo.getSkuId();
                    //商品id一样
                    if (skuId.equals(skuIdTemp)){
                        cartInfo.setSkuNum(cartInfo.getSkuNum() + cartInfoTemp.getSkuNum());//数量相加
                        cartInfo.setIsChecked(cartInfoTemp.getIsChecked());//选中状态
                        cartInfoMapper.updateById(cartInfo);
                        continue cartFor;//结束当次外层循环,进入下一个循环
                    }
                }
                //商品id不一样,说明临时购物车中有正式购物车没有的数据,应该添加
                cartInfoTemp.setId(null);
                cartInfoTemp.setUserId(userId);
                cartInfoMapper.insert(cartInfoTemp);
            }
        }
        return true;
    }

    //删除临时id的购物车
    public void deleteCartList(String userTempId) {
        //删除数据库,删除缓存
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userTempId);
        cartInfoMapper.delete(wrapper);
        String cartKey = getCartKey(userTempId);
        Boolean flag = redisTemplate.hasKey(cartKey);
        if (flag){
            redisTemplate.delete(cartKey);
        }
    }

    //同步缓存
    public void loadCartCacheByUserId(String userId) {
        //从数据库中查出购物车列表数据
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(wrapper);
        HashMap<String, CartInfo> map = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            //查价格,添加到数据中
            BigDecimal skuPrice = productFeignClient.getSkuPrice(cartInfo.getSkuId());
            cartInfo.setSkuPrice(skuPrice);
            map.put(cartInfo.getSkuId() + "",cartInfo);
        }
        String cartKey = this.getCartKey(userId);//创建key
        Boolean flag = redisTemplate.hasKey(cartKey);//查询key是否有值
        if (flag){
            redisTemplate.delete(cartKey);//旧的值,删除旧的值
        }
        redisTemplate.boundHashOps(cartKey).putAll(map);//往缓存中添加新的值
        this.setCartKeyExpire(userId); //设置过期时间
    }

    //根据userTempId查询数据库中是否有购物车数据
    public List<CartInfo> checkIfMergeToCartList(String userTempId) {
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userTempId);
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(wrapper);
        return cartInfoList;
    }

    /**
     *根据用户获取购物车,购物车展示
     */
    public List<CartInfo> getCartList(String userId, String userTempId) {
        if (StringUtil.isEmpty(userId)){
            userId = userTempId;
        }
        //1.  根据用户Id 查询 {先查询缓存，缓存没有，再查询数据库}
        List<CartInfo> cartInfoList = new ArrayList<>();
        //先查缓存
        String cartKey = this.getCartKey(userId);//定义key
        cartInfoList = redisTemplate.opsForHash().values(cartKey);
        if (null == cartInfoList || cartInfoList.size() == 0){
            //缓存中没有,查询数据库
            cartInfoList = this.loadCartCache(userId);
        }
        return cartInfoList;
    }

    /**'
     *根据userId查询数据库中的购物车数据
     */
    public List<CartInfo> loadCartCache(String userId){
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(cartInfoList)){
            return cartInfoList;//无值,return空
        }
        //有值,同步到数据库
        HashMap<String, CartInfo> map = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            //查价格
            BigDecimal skuPrice = productFeignClient.getSkuPrice(cartInfo.getSkuId());
            cartInfo.setSkuPrice(skuPrice);
            map.put(cartInfo.getSkuId()+"",cartInfo);
        }
        String cartKey = this.getCartKey(userId);  //定义key
        redisTemplate.opsForHash().putAll(cartKey, map);//同步缓存
        this.setCartKeyExpire(cartKey);//设置过期时间
        return cartInfoList;
    }

    /**
     *添加购物车
     */
    public void addToCart(Long skuId, String userId, Integer skuNum) {
        //获取购物车的key
        String cartKey = this.getCartKey(userId);
        //根据userId查询购物中是否有数据
        QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id", skuId);
        wrapper.eq("user_id", userId);
        CartInfo cartInfoDB = cartInfoMapper.selectOne(wrapper);
        //有数据
        if (null != cartInfoDB){
            //数量相加
            cartInfoDB.setSkuNum(cartInfoDB.getSkuNum() + skuNum);
            //查询最新价格
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            cartInfoDB.setSkuPrice(skuPrice);
            //更新价格和价格
            cartInfoMapper.updateById(cartInfoDB);
        }else {
            //数据库无数据,添加订单
            CartInfo cartInfo = new CartInfo();
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setSkuId(skuId);
            cartInfo.setUserId(userId);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            //添加数据库
            cartInfoMapper.insert(cartInfo);
            cartInfoDB = cartInfo;
        }
        //更新缓存
        redisTemplate.boundHashOps(cartKey).put(skuId.toString(), cartInfoDB);
        //设置过期时间
        setCartKeyExpire(cartKey);
    }

    /**
     *获取购物车的key
     */
    public String getCartKey(String userId){
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }

    /**
     *设置过期时间
     */
    public void setCartKeyExpire(String cartKey){
        redisTemplate.expire(cartKey, RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
    }
}
