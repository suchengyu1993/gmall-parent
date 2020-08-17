package com.suchengyu.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suchengyu.gmall.model.user.UserAddress;
import com.suchengyu.gmall.model.user.UserInfo;
import com.suchengyu.gmall.user.mapper.UserAddressMapper;
import com.suchengyu.gmall.user.mapper.UserInfoMapper;
import com.suchengyu.gmall.user.service.UserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * UserApiServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-04
 * @Description:
 */
@Service
public class UserApiServiceImpl implements UserApiService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;

    //获取用户地址
    public List<UserAddress> getUserAddresses(String userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<UserAddress> userAddressList = userAddressMapper.selectList(wrapper);
        return userAddressList;
    }

    //登录成功往redis中存入token,返回token
    @Override
    public UserInfo login(UserInfo userInfo) {
        //校验用户名和密码
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("login_name", userInfo.getLoginName());
        //注意密码是使用MD5加密的
        wrapper.eq("passwd", DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes()));
        UserInfo userInfoReturn = userInfoMapper.selectOne(wrapper);
        if (null != userInfoReturn){
            return userInfoReturn;
        }
        return null;
    }

    //根据token从缓存获取userId
    public String getUserId(String token) {
        String userId = (String) redisTemplate.opsForValue().get("user:token:" + token);
        return userId;
    }
}
