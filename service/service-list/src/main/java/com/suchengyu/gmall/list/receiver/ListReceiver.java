package com.suchengyu.gmall.list.receiver;

import com.rabbitmq.client.Channel;
import com.suchengyu.gmall.common.constant.MqConst;
import com.suchengyu.gmall.list.service.ListApiService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * ListReceiver
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-16
 * @Description:
 */
@Component
public class ListReceiver {

    @Autowired
    private ListApiService listApiService;

    /**
     * 商品上架
     * @param skuId
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
           value = @Queue(value = MqConst.QUEUE_GOODS_UPPER,declare = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_GOODS),
            key = (MqConst.ROUTING_GOODS_UPPER)
    ))
    public void upperGoods(Long skuId, Message message, Channel channel){
        if (null != skuId && skuId > 0){
            listApiService.upperGoods(skuId);
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_GOODS_LOWER,declare = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_GOODS),
            key = (MqConst.ROUTING_GOODS_LOWER)
    ))
    public void lowerGoods(Long skuId,Message message,Channel chanel){
        if (null != skuId && skuId > 0){
            listApiService.lowerGoods(skuId);
        }
        try {
            chanel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
