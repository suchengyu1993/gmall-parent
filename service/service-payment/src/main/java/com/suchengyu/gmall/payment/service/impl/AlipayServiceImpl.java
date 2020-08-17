package com.suchengyu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.suchengyu.gmall.common.service.RabbitService;
import com.suchengyu.gmall.model.enums.PaymentType;
import com.suchengyu.gmall.model.order.OrderInfo;
import com.suchengyu.gmall.order.client.OrderFeignClient;
import com.suchengyu.gmall.payment.config.AlipayConfig;
import com.suchengyu.gmall.payment.receiver.PaymentDelayMqConfig;
import com.suchengyu.gmall.payment.service.AlipayService;
import com.suchengyu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * AlipayServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-11
 * @Description:
 */
@Service
public class AlipayServiceImpl implements AlipayService {
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private RabbitService rabbitService;

    //主动调用支付接口,返回支付状态
    public String checkAliStatus(String outTradeNo) {
        //调用支付接口,返回支付状态
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();//创建对应的请求
        //同步回调
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        //异步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址
        //参数设置
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no",outTradeNo);//订单交易编号
        alipayRequest.setBizContent(JSON.toJSONString(map));
        try {
            AlipayTradeQueryResponse  response = alipayClient.execute(alipayRequest);
            if(response.isSuccess()){
                System.out.println("调用成功");
                String tradeStatus = response.getTradeStatus();
                System.out.println(tradeStatus);
                return tradeStatus;
            } else {
                System.out.println("调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    //生成表单
    public String alipaySubmit(Long orderId) throws AlipayApiException {
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        //保存交易记录
        paymentService.savePaymentInfo(orderInfo,PaymentType.ALIPAY.name());
        //生成二维码
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建对应的请求
        //同步回调
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        //异步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址
        //参数设置
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no", orderInfo.getOutTradeNo());//订单交易编号
        map.put("product_code","FAST_INSTANT_TRADE_PAY" );
//        map.put("total_amount",orderInfo.getTotalAmount() );//总金额
        map.put("total_amount", "0.01");//测试的金额
        map.put("subject","test" );
        alipayRequest.setBizContent(JSON.toJSONString(map));

        //幂等性校验,发送一个延迟队列
        rabbitService.sendDelayMessage(PaymentDelayMqConfig.exchange_delay_payment_check,
                PaymentDelayMqConfig.routing_delay_check,orderInfo.getOutTradeNo() ,30);

        return alipayClient.pageExecute(alipayRequest).getBody();//调用SDK生成表单
    }

}
