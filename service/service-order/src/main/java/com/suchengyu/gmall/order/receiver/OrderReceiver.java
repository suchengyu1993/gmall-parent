package com.suchengyu.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.suchengyu.gmall.common.constant.MqConst;
import com.suchengyu.gmall.model.enums.ProcessStatus;
import com.suchengyu.gmall.model.order.OrderInfo;
import com.suchengyu.gmall.order.service.OrderApiService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;


/**
 * OrderReceiver
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-15
 * @Description:接收信息
 */
@Component
public class OrderReceiver {
    @Autowired
    private OrderApiService orderApiService;

    /**
     * 减库存成功,更新库存状态
     * @param megJson
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_WARE_ORDER,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_WARE_ORDER),
            key = {MqConst.ROUTING_WARE_ORDER}
    ))
    public void updateOrderStatus(String megJson,Message message,Channel channel){
        if (!StringUtils.isEmpty(megJson)){
            Map<String,Object> map = JSON.parseObject(megJson, Map.class);
            String orderId = (String) map.get("orderId");
            String status = (String) map.get("status");
            if ("DEDUCTED" .equals(status)){
                //减库存成功,修改订单状态为等待发货 ProcessStatus.WAITING_DELEVER 为待发货
                orderApiService.updateOrderStatus(Long.parseLong(orderId),ProcessStatus.WAITING_DELEVER);
            }else{
                /*
                    减库存失败！远程调用其他仓库查看是否有库存！
                    true:   orderApiService.sendOrderStatus(orderId); orderApiService.updateOrderStatus(orderId, ProcessStatus.NOTIFIED_WARE);
                    false:  1.  补货  | 2.   人工客服。
                */
                //  ProcessStatus.STOCK_EXCEPTION 为库存异常
                orderApiService.updateOrderStatus(Long.parseLong(orderId), ProcessStatus.STOCK_EXCEPTION);
            }
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订单支付,更改订单状态与通知减库存
     * @param orderId
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_PAYMENT_PAY),
            key = {MqConst.ROUTING_PAYMENT_PAY}
    ))
    public void paySuccess(Long orderId,Message message,Channel channel){
        if (null != orderId){
            OrderInfo orderInfo = orderApiService.getOrderInfo(orderId);
            //状态为未支付才可以更改订单
            if (null != orderInfo && orderInfo.getProcessStatus().equals(ProcessStatus.UNPAID.getOrderStatus().name())){
                //支付成功,修改订单状态为已支付
                orderApiService.updateOrderStatus(orderId, ProcessStatus.PAID);
                //发送信息,通知仓库减库存
                orderApiService.sendOrderStatus(orderId);
            }
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消订单消费者
     * 延迟队列,不能在这里做交换机与队列绑定
     * @param orderId
     * @param message
     * @param channel
     */
    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void orderCancel(Long orderId, Message message, Channel channel){
        if (null != orderId || orderId > 0){
            OrderInfo orderInfo = orderApiService.getOrderInfo(orderId);
            //防止重复消费,订单为未支付才能进来
            if (null != orderInfo &&
                    orderInfo.getOrderStatus().equals(ProcessStatus.UNPAID.getOrderStatus().name())){
                orderApiService.execExpiredOrder(orderId);
            }
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
