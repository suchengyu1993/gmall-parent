package com.suchengyu.gmall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suchengyu.gmall.common.constant.MqConst;
import com.suchengyu.gmall.common.service.RabbitService;
import com.suchengyu.gmall.model.enums.PaymentStatus;
import com.suchengyu.gmall.model.enums.PaymentType;
import com.suchengyu.gmall.model.enums.ProcessStatus;
import com.suchengyu.gmall.model.order.OrderInfo;
import com.suchengyu.gmall.model.payment.PaymentInfo;
import com.suchengyu.gmall.payment.mapper.PaymentInfoMapper;
import com.suchengyu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * PaymentServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-11
 * @Description:
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;
    @Autowired
    private RabbitService rabbitService;

    //修改支付状态
    public void updatePayment(PaymentInfo paymentInfo) {
        // 支付状态修改为已支付
        PaymentInfo paymentInfoDB = this.getPaymentInfo(paymentInfo.getOutTradeNo(), PaymentType.ALIPAY.name());
        //当支付状态为已支付或已关闭时,不修改支付记录
        if (paymentInfoDB.getPaymentStatus() == PaymentStatus.PAID.name()
                || paymentInfoDB.getPaymentStatus() == PaymentStatus.ClOSED.name()){
            return;
        }
        PaymentInfo paymentInfo2 = new PaymentInfo();
        paymentInfo2.setCallbackTime(new Date());
        paymentInfo2.setPaymentStatus(PaymentStatus.PAID.name());
        paymentInfo2.setCallbackContent("测试成功啊");
        this.updatePaymentInfo(paymentInfo.getOutTradeNo(), paymentInfo2);

        // TODO 支付成功,同步回调,后续更新订单状态,使用正常消息队列
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,
                MqConst.ROUTING_PAYMENT_PAY, paymentInfoDB.getOrderId());

    }

    //根据第三方交易编号,修改支付交易记录
    public void updatePaymentInfo(String outTradeNo, PaymentInfo paymentInfo) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no", outTradeNo);
        paymentInfoMapper.update(paymentInfo, wrapper);
    }

    //支付成功,修改支付记录
    public void paySuccess(String out_trade_no, String paymentType, Map<String, String> paramMap) {
        PaymentInfo paymentInfoDB = this.getPaymentInfo(out_trade_no, paymentType);
        //当支付状态为已支付或已关闭时,不修改支付记录
        if (paymentInfoDB.getPaymentStatus() == PaymentStatus.PAID.name()
                || paymentInfoDB.getPaymentStatus() == PaymentStatus.ClOSED.name()){
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(ProcessStatus.PAID.name());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(paramMap.toString());
        this.updatePaymentInfo(out_trade_no, paymentInfo);

        // TODO 支付成功,异步回调,后续更新订单状态,使用正常消息队列
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,
                MqConst.ROUTING_PAYMENT_PAY, paymentInfoDB.getOrderId());
    }


    //获取交易记录信息
    public PaymentInfo getPaymentInfo(String out_trade_no, String paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no", out_trade_no).eq("payment_type", paymentType);
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(wrapper);
        return paymentInfo;
    }

    //保存交易记录
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderInfo.getId());
        wrapper.eq("payment_type", paymentType);
        Integer integer = paymentInfoMapper.selectCount(wrapper);
        if (integer > 0 ){//生成过订单,直接返回
            return;
        }
        //保存交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());//订单生成的时间
        paymentInfo.setOrderId(orderInfo.getId());//订单
        paymentInfo.setPaymentType(paymentType);//交易类型
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());//交易编号
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());//交易状态
        paymentInfo.setSubject(orderInfo.getTradeBody());//交易内容
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());//交易金额
        paymentInfoMapper.insert(paymentInfo);//保存
    }
}
