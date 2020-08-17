package com.suchengyu.gmall.user.client;

import com.suchengyu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * UserFeignClient
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-04
 * @Description:
 */
@FeignClient("service-user")
public interface UserFeignClient {

    @RequestMapping("/api/user/passport/inner/getUserId/{token}")
    String getUserId(@PathVariable("token") String token);

    //获取用户地址
    @RequestMapping("/api/user/passport/inner/getUserAddresses/{userId}")
    List<UserAddress> getUserAddresses(@PathVariable("userId") String userId);

}
