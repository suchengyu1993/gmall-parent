package com.suchengyu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suchengyu.gmall.common.cache.GmallCache;
import com.suchengyu.gmall.common.constant.MqConst;
import com.suchengyu.gmall.common.constant.RedisConst;
import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.common.service.RabbitService;
import com.suchengyu.gmall.list.client.ListFeignClient;
import com.suchengyu.gmall.model.product.SkuAttrValue;
import com.suchengyu.gmall.model.product.SkuImage;
import com.suchengyu.gmall.model.product.SkuInfo;
import com.suchengyu.gmall.model.product.SkuSaleAttrValue;
import com.suchengyu.gmall.product.mapper.SkuAttrValueMapper;
import com.suchengyu.gmall.product.mapper.SkuImageMapper;
import com.suchengyu.gmall.product.mapper.SkuInfoMapper;
import com.suchengyu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.suchengyu.gmall.product.service.SkuService;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * SkuServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-17
 * @Description:
 */
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ListFeignClient listFeignClient;
    @Autowired
    private RabbitService rabbitService;

    //根据skuId从数据库中查询商品基本信息
    @GmallCache(prefix = "sku:",suffix = ":info") //表示自动使用缓存
    public SkuInfo getSkuInfoAop(Long skuId) {
        //缓存没有数据,从数据库中查询
        SkuInfo skuInfo = null;
        skuInfo = skuInfoMapper.selectById(skuId);
        System.out.println("执行了目标方法");
        if (null != skuInfo) {
            //查询图片集合,添加到基本信息
            QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
            wrapper.eq("sku_id", skuId);
            System.out.println("查询图片信息,跟基本信息共用缓存");
            List<SkuImage> skuImageList = skuImageMapper.selectList(wrapper);
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }


    //根据skuId使用缓存查询商品基本信息(简单的模式,容易被用不存在的key频繁访问数据库)
    public SkuInfo getSkuInfoNx(Long skuId) {
        // 1. 定义缓存中的key
        SkuInfo skuInfo = null;
       String skuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        // 2. 根据key从缓存中查询数据,查到数据,直接返回结果
        String skuCache = (String) redisTemplate.opsForValue().get(skuKey);
        if (StringUtil.isNotBlank(skuCache)){
            skuInfo = JSON.parseObject(skuCache, SkuInfo.class);
        }else {
            // 3. 没查到数据,从db中查询数据
            // 3.1 制作带过期时间的分布式锁的key
            String uuid = UUID.randomUUID().toString();
            String stock = RedisConst.SKUKEY_PREFIX + skuId + ":stock";
            Boolean stockLock = redisTemplate.opsForValue().setIfAbsent(stock, uuid, 1, TimeUnit.SECONDS);//1秒钟过期
            if (stockLock){
                // 3.2 拿到分布式锁的key可以查db
                skuInfo = getSkuInfoFromDB(skuId);
                if (null != skuInfo){
                    // 3.2.1 db中查到值,放入缓存中
                    redisTemplate.opsForValue().set(skuKey, JSON.toJSONString(skuInfo));
                }else {
                    // 3.2.2 访问不存在的key，放置空对象到redis中，防止缓存穿透
                    redisTemplate.opsForValue().set(skuKey,JSON.toJSONString(new SkuInfo()),10,TimeUnit.SECONDS);//10秒过期
                }
                // 3.3 操作都完成,使用lua脚本删除key
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 设置lua脚本返回的数据类型
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                // 设置lua脚本返回类型为Long
                 redisScript.setResultType(Long.class);
                redisScript.setScriptText(script);
                redisTemplate.execute(redisScript, Arrays.asList(stock),uuid);
            }else {
                // 4. 没拿到分布式锁的key,等待,进入自旋
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuInfoNx(skuId);
            }
        }
        return skuInfo;
    }

    //根据skuId使用缓存查询商品基本信息(简单的模式,容易被用不存在的key频繁访问数据库)
    public SkuInfo getSkuInfo(Long skuId) {
        // 1. 先根据key查询缓存是否有数据
        String skuKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        SkuInfo skuInfo = null;
        //查询缓存,判断是否有值
        String skuCache = (String) redisTemplate.opsForValue().get(skuKey);
        if (!StringUtils.isEmpty(skuCache)) {
            //有值,转换成对象返回
            skuInfo = JSON.parseObject(skuCache, SkuInfo.class);
        } else {
            //无值,从db中获取
             skuInfo = getSkuInfoFromDB(skuId);
            //判断从数据库获取值是否为空,不为空则放进缓存
            if (null != skuInfo) {
                redisTemplate.opsForValue().set(skuKey, JSON.toJSONString(skuInfo));
            }
        }
        return skuInfo;
    }

    //根据skuId从数据库中查询商品基本信息
    public SkuInfo getSkuInfoFromDB(Long skuId) {
        //缓存没有数据,从数据库中查询
        SkuInfo skuInfo = null;
        skuInfo = skuInfoMapper.selectById(skuId);
        if (null != skuInfo) {
            //查询图片集合,添加到基本信息
            QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
            wrapper.eq("sku_id", skuId);
            List<SkuImage> skuImageList = skuImageMapper.selectList(wrapper);
            skuInfo.setSkuImageList(skuImageList);
        }
        return skuInfo;
    }
    //商品销售属性对应的skuId的map
    @GmallCache(prefix = "skuIds:",suffix = ":map")
    public List<Map<String, Object>> getSkuValueIdsMap(Long spuId) {
        List<Map<String, Object>> maps = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);
        return maps;
    }
    //根据skuId查询价格
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }
    //Sku商品下架
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        int updateById = skuInfoMapper.updateById(skuInfo);
        if (updateById > 0) {
            Result.ok().message("商品下架成功");
        } else {
            Result.fail().message("商品下架失败");
        }
        // TODO 使用RabbitMq发送消息商品下架
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS, MqConst.ROUTING_GOODS_LOWER, skuId);

        //待补充,下架的sku数据从es中移除
//        listFeignClient.lowerGoods(skuId);
    }
    //Sku商品上架
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        int updateById = skuInfoMapper.updateById(skuInfo);
        if (updateById > 0) {
            Result.ok().message("商品上架成功");
        } else {
            Result.fail().message("商品上架失败");
        }
        // TODO 使用RabbitMq发送消息商品上架
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS, MqConst.ROUTING_GOODS_UPPER, skuId);
        //上架的sku数据保存到es中
//        listFeignClient.upperGoods(skuId);
    }
    //分页显示Sku列表
    public IPage<SkuInfo> list(Page skuInfoPage) {
        IPage iPage = skuInfoMapper.selectPage(skuInfoPage, null);
        return iPage;
    }
    //Sku保存功能
    public void saveSkuInfo(SkuInfo skuInfo) {
        // 1. 保存sku基本信息
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();
        Long spuId = skuInfo.getSpuId();
        // 2. 保存sku商品属性
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }
        // 3. 保存sku商品属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSpuId(spuId);
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }
        // 4. 保存sku图片集合
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }
    }
}
