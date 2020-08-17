package com.suchengyu.gmall.all.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.common.util.AuthContextHolder;
import com.suchengyu.gmall.order.client.OrderFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * OrderController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-07
 * @Description:
 */
@Api(description = "订单功能")
@Controller
public class OrderController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @ApiOperation(value = "购物车结算功能,点击生成订单")
    @GetMapping("/trade.html")
    public String trade (Model model, HttpServletRequest request){
        //生成交易码
        String userId = AuthContextHolder.getUserId(request);
        String tradeNo = orderFeignClient.getTradeNo(userId);
        Result<Map<String,Object>> map =  orderFeignClient.trade();
        model.addAttribute("tradeNo", tradeNo);
        model.addAllAttributes(map.getData()) ;
        return "order/trade";
    }


}
