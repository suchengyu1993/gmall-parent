package com.suchengyu.gmall.order.client;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * OrderFeignClient
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-07
 * @Description:
 */
@FeignClient("service-order")
public interface OrderFeignClient {

    //购物车结算
    @GetMapping("/api/order/auth/trade")
    Result<Map<String, Object>> trade();

    //生成交易码
    @GetMapping("/api/order/auth/getTradeNo/{userId}")
    String getTradeNo(@PathVariable("userId") String userId);

    //提交订单,跳转到支付页面
    @GetMapping("/api/order/auth/getOrderInfo/{orderId}")
    OrderInfo getOrderInfo(@PathVariable("orderId") long orderId);
}
