package com.suchengyu.gmall.payment.receiver;

import com.rabbitmq.client.Channel;
import com.suchengyu.gmall.common.constant.MqConst;
import com.suchengyu.gmall.common.service.RabbitService;
import com.suchengyu.gmall.model.enums.PaymentStatus;
import com.suchengyu.gmall.model.enums.PaymentType;
import com.suchengyu.gmall.model.payment.PaymentInfo;
import com.suchengyu.gmall.payment.service.AlipayService;
import com.suchengyu.gmall.payment.service.PaymentService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;


/**
 * PaymentReceiver
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-17
 * @Description:
 */
@Component
public class PaymentReceiver {
    @Autowired
    private AlipayService alipayService;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private PaymentService paymentService;

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = PaymentDelayMqConfig.queue_delay_payment_check,autoDelete = "true"),
//            exchange = @Exchange(value = PaymentDelayMqConfig.exchange_delay_payment_check,autoDelete = "true"),
//            key = {PaymentDelayMqConfig.routing_delay_check}))
    @RabbitListener(queues = PaymentDelayMqConfig.queue_delay_payment_check)
    public  void payCheck(String OutTradeNo, Message message, Channel channel) throws IOException {
        System.out.println("支付系统服务,检查支付状态");
        //调用支付接口,返回支付状态
        String trade_status = alipayService.checkAliStatus(OutTradeNo);
        if (!StringUtils.isEmpty(trade_status) && (("TRADE_SUCCESS").equals(trade_status) || ("TRADE_FINISHED").equals(trade_status))){
            //用户支付成功进行幂等性检查
            PaymentInfo paymentInfo = paymentService.getPaymentInfo(OutTradeNo, PaymentType.ALIPAY.name());
            //当支付状态为已支付或已关闭时,不修改支付记录
            if (null != paymentInfo && (paymentInfo.getPaymentStatus() == PaymentStatus.PAID.name()
                    || paymentInfo.getPaymentStatus() == PaymentStatus.ClOSED.name())){
                return ;
            }
            //更新支付状态
            PaymentInfo paymentInfo2 = new PaymentInfo();
            paymentInfo2.setCallbackTime(new Date());
            paymentInfo2.setPaymentStatus(PaymentStatus.PAID.name());
            paymentInfo2.setCallbackContent("幂等性检查成功,修改订单状态");
            paymentService.updatePaymentInfo(OutTradeNo, paymentInfo2);

            // TODO 支付成功,同步回调,后续更新订单状态,使用正常消息队列
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,
                    MqConst.ROUTING_PAYMENT_PAY, paymentInfo.getOrderId());

        }else if (!StringUtils.isEmpty(trade_status) && ("WAIT_BUYER_PAY".equals(trade_status))){
            //如果未付款,重新发延迟队列检查
            System.out.println("用户尚在付款中,继续发送延迟队列");
            rabbitService.sendDelayMessage(PaymentDelayMqConfig.exchange_delay_payment_check, PaymentDelayMqConfig.routing_delay_check, OutTradeNo, 30);
        }else {
            rabbitService.sendDelayMessage(PaymentDelayMqConfig.exchange_delay_payment_check, PaymentDelayMqConfig.routing_delay_check, OutTradeNo, 30);
        }
        //消费完成,确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
