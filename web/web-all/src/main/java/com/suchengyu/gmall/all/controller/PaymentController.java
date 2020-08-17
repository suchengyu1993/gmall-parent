package com.suchengyu.gmall.all.controller;

import com.suchengyu.gmall.model.order.OrderInfo;
import com.suchengyu.gmall.order.client.OrderFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * PaymentController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-08
 * @Description:
 */
@Api(description = "web的调用订单接口")
@Controller
public class PaymentController {

    @Autowired
    private OrderFeignClient orderFeignClient;


    @ApiOperation(value = "回调信息,跳转到支付成功页面")
    @GetMapping("/pay/success.html")
    public String success(){
        return "payment/success";
    }

    @ApiOperation(value = "提交订单,跳转到支付页面")
    @RequestMapping("/pay.html")
    public String pay(HttpServletRequest request,Model model, String orderId){
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(Long.parseLong(orderId));
        model.addAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }
}
