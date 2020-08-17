package com.suchengyu.gmall.payment.service;

import com.suchengyu.gmall.model.order.OrderInfo;
import com.suchengyu.gmall.model.payment.PaymentInfo;

import java.util.Map;

/**
 * PaymentService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-11
 * @Description:
 */
public interface PaymentService {

    /**
     * 保存交易记录
     * @param orderInfo
     * @param paymentType 支付类型(1:微信    2:支付宝)
     */
    void savePaymentInfo(OrderInfo orderInfo, String paymentType);

    //获取交易记录信息
    PaymentInfo getPaymentInfo(String out_trade_no, String paymentType);

    //支付成功
    void paySuccess(String out_trade_no, String paymentType, Map<String, String> paramMap);

    //根据第三方交易编号,修改支付交易记录
    void updatePaymentInfo(String outTradeNo,PaymentInfo paymentInfo);

    //修改支付状态
    void updatePayment(PaymentInfo paymentInfo);

}
