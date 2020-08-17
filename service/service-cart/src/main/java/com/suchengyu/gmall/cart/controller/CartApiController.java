package com.suchengyu.gmall.cart.controller;

import com.suchengyu.gmall.cart.service.CartAplService;
import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.common.util.AuthContextHolder;
import com.suchengyu.gmall.model.cart.CartInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * CartAplController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-06
 * @Description:
 */
@Api(description = "购物车后端功能")
@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    @Autowired
    private CartAplService cartAplService;

    @ApiOperation(value = "重新查询价格,更新缓存")
    @GetMapping("/api/cart/loadCartCache/{userId}")
    public Result loadCartCache(@PathVariable("userId") String userId){
        cartAplService.loadCartCacheByUserId(userId);
        return Result.ok();
    }

    @ApiOperation(value = "用户已经登录才能访问结算获得购物车列表转化成订单详情信息")
    @RequestMapping("/getCartList/{userId}")
    public List<CartInfo> getCartList(@PathVariable("userId") String userId){
        List<CartInfo> cartList = cartAplService.getCartList(userId, null);
        return cartList;
    }

    @ApiOperation(value = "根据skuId删除购物车")
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId,HttpServletRequest request){
        //获取用户id
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)){
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartAplService.deleteCart(userId,skuId);//调用删除方法
        return Result.ok();
    }

    @ApiOperation(value = "更改购物车选择状态")
    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable("skuId")Long skuId,
                            @PathVariable("isChecked")Integer isChecked,HttpServletRequest request){
        //获取用户Id
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)){
            userId = AuthContextHolder.getUserTempId(request);
        }
        //调用更新方法
        cartAplService.checkCart(userId,isChecked,skuId);
        return Result.ok();
    }

    @ApiOperation(value = "合并购物车操作")
    @GetMapping("/mergeToCartList/{userId}/{userTempId}")
    public void mergeToCartList(@PathVariable("userId")String userId, @PathVariable("userTempId")String userTempId){
        //合并正式购物车
        boolean flag = cartAplService.mergeToCartList(userId, userTempId);
        if (flag){
            //删除临时id的购物车
            cartAplService.deleteCartList(userTempId);
            //同步缓存
            cartAplService.loadCartCacheByUserId(userId);
        }
    }

   @ApiOperation(value = "根据userTempId查询数据库中是否有购物车数据")
   @GetMapping("/checkIfMergeToCartList/{userTempId}")
   public boolean checkIfMergeToCartList(@PathVariable("userTempId") String userTempId){
        boolean flag = false;
        List<CartInfo> cartInfoList = cartAplService.checkIfMergeToCartList(userTempId);
        if (null != cartInfoList && cartInfoList.size() > 0){
            //查出数据就return true,才可以合并购物车
            flag = true;
        }
        return flag;
    }


    @ApiOperation(value = "查询购物车列表")
    @GetMapping("/cartList")
    public Result cartList(
            @ApiParam(name = "request" ,value = "请求头中携带有userId或临时UserTempId",required =true )
            HttpServletRequest request){
        //获取用户id和临时id
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);
        List<CartInfo> cartInfoList = cartAplService.getCartList(userId,userTempId);
        return Result.ok(cartInfoList);
    }


    @ApiOperation(value = "添加购物车")
    @PostMapping("/addToCart/{skuId}/{skuNum}")
    public Result addToCart(
            @ApiParam(name = "skuId" ,value = "商品id",required =true )
            @PathVariable("skuId") Long skuId,
            @ApiParam(name = "skuNum" ,value = "商品数量",required =true )
            @PathVariable("skuNum") Integer skuNum,
            @ApiParam(name = "request" ,value = "请求头中携带有userId或临时UserTempId",required =true )
            HttpServletRequest request){
        //获取userId
        String userId = AuthContextHolder.getUserId(request);
        //无userId就获取临时用户id
        if (StringUtil.isEmpty(userId)){
             userId = AuthContextHolder.getUserTempId(request);
        }
        cartAplService.addToCart(skuId,userId,skuNum);
        return Result.ok();
    }
}
