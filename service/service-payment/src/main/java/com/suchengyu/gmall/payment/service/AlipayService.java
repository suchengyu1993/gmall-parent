package com.suchengyu.gmall.payment.service;

import com.alipay.api.AlipayApiException;

/**
 * AlipayService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-11
 * @Description:
 */
public interface AlipayService {
    //生成表单
    String alipaySubmit(Long orderId) throws AlipayApiException;

    //调用支付接口,返回支付状态
    String checkAliStatus(String outTradeNo);
}
