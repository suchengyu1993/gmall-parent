package com.suchengyu.gmall.user.controller;

import com.suchengyu.gmall.cart.client.CartFeignClient;
import com.suchengyu.gmall.common.constant.RedisConst;
import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.common.util.AuthContextHolder;
import com.suchengyu.gmall.model.user.UserAddress;
import com.suchengyu.gmall.model.user.UserInfo;
import com.suchengyu.gmall.user.service.UserApiService;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * UserApiController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-04
 * @Description:
 */
@RestController
@RequestMapping("/api/user/passport")
public class UserApiController {
    @Autowired
    private UserApiService userApiService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CartFeignClient cartFeignClient;

    //获取用户地址
    @RequestMapping("/inner/getUserAddresses/{userId}")
    public List<UserAddress> getUserAddresses(@PathVariable("userId") String userId){
        List<UserAddress> userAddressList = userApiService.getUserAddresses(userId);
        return userAddressList;
    }

    @ApiOperation(value = "登录成功往redis中存入token,返回token")
    @RequestMapping("/login")
    public Result login(HttpServletRequest request, @RequestBody UserInfo userInfo){
        UserInfo info = userApiService.login(userInfo);
        if (null != info){
            //生成UUID,放入Redis缓存中
            String token = UUID.randomUUID().toString().replaceAll("-","");
            Map<String, Object> map = new HashMap<>();
            map.put("name",info.getName() );
            map.put("nickName", info.getNickName());//昵称
            map.put("token",token);
            redisTemplate.opsForValue().set("user:token:"+ token,info.getId().toString(),RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
            //调用购物车的feign合并接口
//            Long userId = info.getId();
            String userId = info.getId() + "";
            String userTempId = AuthContextHolder.getUserTempId(request);
            //调用购物车合并功能
            boolean flag = false;
            if (StringUtil.isNotBlank(userTempId)){
                 flag = cartFeignClient.checkIfMergeToCartList(userTempId);//根据userTempId查询数据库中是否有购物车数据
            }
            if(flag){
                //有数据就合并
                cartFeignClient.mergeToCartList(userId,userTempId);
            }
            return Result.ok(map);
        }else {
            return Result.fail().message("用户名或密码错误");
        }
    }


    @ApiOperation(value = "根据token从缓存获取userId")
    @RequestMapping("/inner/getUserId/{token}")
   public String getUserId(@PathVariable("token") String token){
        String userId = userApiService.getUserId(token);
        return userId;
    }
}
