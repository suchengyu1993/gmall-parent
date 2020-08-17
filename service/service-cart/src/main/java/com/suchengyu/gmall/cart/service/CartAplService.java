package com.suchengyu.gmall.cart.service;

import com.suchengyu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * CartAplService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-06
 * @Description:
 */
public interface CartAplService {
    //添加购物车
    void addToCart(Long skuId, String userId, Integer skuNum);

    //根据用户获取购物车
    List<CartInfo> getCartList(String userId, String userTempId);

    //根据userTempId查询数据库中是否有购物车数据
    List<CartInfo> checkIfMergeToCartList(String userTempId);

    //合并正式购物车
    boolean mergeToCartList(String userId, String userTempId);

    //删除临时id的购物车
    void deleteCartList(String userTempId);

    //同步缓存
    void loadCartCacheByUserId(String userId);

    //更改购物车选择状态
    void checkCart(String userId, Integer isChecked, Long skuId);

    //根据skuId删除购物车
    void deleteCart(String userId, Long skuId);

}
