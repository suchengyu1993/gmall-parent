package com.suchengyu.gmall.user.service;

import com.suchengyu.gmall.model.user.UserAddress;
import com.suchengyu.gmall.model.user.UserInfo;

import java.util.List;

/**
 * UserApiService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-04
 * @Description:
 */
public interface UserApiService {

    //根据token从缓存获取userId
    String getUserId(String token);


    //登录成功往redis中存入token,返回token
    UserInfo login(UserInfo userInfo);

    //获取用户地址
    List<UserAddress> getUserAddresses(String userId);

}
