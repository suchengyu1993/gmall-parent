package com.suchengyu.gmall.order.service;

import com.suchengyu.gmall.model.enums.ProcessStatus;
import com.suchengyu.gmall.model.order.OrderInfo;

import java.util.Map;

/**
 * OrderApiService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-10
 * @Description:
 */

public interface OrderApiService {
    //提交订单
    Long saveOrderInfo(OrderInfo orderInfo);

    //生成交易码
    String getTradeNo(String userId);

    //比较交易码
    boolean checkTradeCode(String userId, String tradeNo);

    //删除交易码
    void deleteTradeNo(String userId);

    /**
     *验证库存
     */
    boolean checkStock(Long skuId,Integer skuNum);

    //提交订单,跳转到支付页面
    OrderInfo getOrderInfo(long orderId);

    //处理过期订单
    void execExpiredOrder(Long orderId);

    //根据订单id,修改订单的状态
    void updateOrderStatus(Long orderId, ProcessStatus processStatus);

    //支付成功,通知仓库减库存
    void sendOrderStatus(Long orderId);

}
