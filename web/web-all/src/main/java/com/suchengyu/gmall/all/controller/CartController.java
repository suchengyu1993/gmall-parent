package com.suchengyu.gmall.all.controller;

import com.suchengyu.gmall.cart.client.CartFeignClient;
import com.suchengyu.gmall.model.product.SkuInfo;
import com.suchengyu.gmall.product.controller.ProductFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * cartController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-06
 * @Description:
 */
@Api(description = "前端购物车功能")
@Controller
public class CartController {
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private ProductFeignClient productFeignClient;

    @ApiOperation(value = "查看购物车列表")
    @RequestMapping("/cart.html")
    public String cartList(HttpServletRequest request){
        return "cart/index";
    }

    @ApiOperation(value = "添加购物车功能")
    @RequestMapping("/addCart.html")
    public ModelAndView addCart(
        @ApiParam(name = "skuId" ,value = "商品id",required =true )
        @RequestParam("skuId") Long skuId,
        @ApiParam(name = "skuNum" ,value = "商品数量",required =true )
        @RequestParam("skuNum")Integer skuNum){
        //通过feign调用cart模块添加购物车
        cartFeignClient.addToCart(skuId,skuNum);
        //通过feign调用product模块查询商品信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //将商品信息跟数量添加到ModelAndView
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("skuNum",skuNum);
        modelAndView.addObject("skuName",skuInfo.getSkuName());
        modelAndView.addObject("skuDefaultImg",skuInfo.getSkuDefaultImg());
        modelAndView.addObject("price",skuInfo.getPrice());
        modelAndView.addObject("skuInfo",skuInfo);
        modelAndView.setViewName("redirect:http://cart.gmall.com/cartSuccess");//重定向到静态页
        return modelAndView;
    }

    //拦截添加购物车的静态页,然后转发到添加成功页面
    @RequestMapping("/cartSuccess")
    public String cartSuccess(SkuInfo skuInfo, Integer skuNum, ModelMap modelMap){
        modelMap.put("skuInfo", skuInfo);
        modelMap.put("skuNum", skuNum);
        return "cart/addCart";
    }
}
