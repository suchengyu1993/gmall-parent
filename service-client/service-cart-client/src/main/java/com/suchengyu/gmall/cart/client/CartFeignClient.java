package com.suchengyu.gmall.cart.client;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * CartFeignClient
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-06
 * @Description:
 */
@FeignClient("service-cart")
public interface CartFeignClient {
    //添加购物车
    @PostMapping("/api/cart/addToCart/{skuId}/{skuNum}")
    Result addToCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Integer skuNum);

    //根据userTempId查询数据库中是否有购物车数据
    @GetMapping("/api/cart/checkIfMergeToCartList/{userTempId}")
    boolean checkIfMergeToCartList(@PathVariable("userTempId") String userTempId);

    //合并购物车操作
    @GetMapping("/api/cart/mergeToCartList/{userId}/{userTempId}")
    void mergeToCartList(@PathVariable("userId")String userId, @PathVariable("userTempId")String userTempId);

    //得到用户想要购买的商品
    @RequestMapping("/api/cart/getCartList/{userId}")
    List<CartInfo> getCartList(@PathVariable("userId")String userId);

    //重新查询价格,更新缓存
    @GetMapping("/api/cart/loadCartCache/{userId}")
    Result loadCartCache(@PathVariable("userId") String userId);
}
