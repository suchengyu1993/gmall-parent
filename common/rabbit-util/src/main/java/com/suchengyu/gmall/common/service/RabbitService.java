package com.suchengyu.gmall.common.service;

import com.alibaba.fastjson.JSON;
import com.suchengyu.gmall.common.entity.GmallCorrelationData;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * sendMessage
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-10
 * @Description:
 */
@Service
public class RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    //过期时间:分钟
    public static final int OBJECT_TIMEOUT = 10;
    /**
     *  发送消息
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        GmallCorrelationData correlationData = new GmallCorrelationData();
        String correlationId = UUID.randomUUID().toString().replaceAll("-", "");
        correlationData.setId(correlationId);
        correlationData.setMessage(message);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        //将消息添加进缓存,设置10分钟过期时间
        redisTemplate.opsForValue().set(correlationId, JSON.toJSONString(correlationData),OBJECT_TIMEOUT, TimeUnit.MINUTES);
        rabbitTemplate.convertAndSend(exchange, routingKey, message,correlationData);//发送信息
        return true;
    }

    /**
     *  发送延迟消息
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息
     * @param delayTime 过期时间,单位秒
     */
    public boolean sendDelayMessage(String exchange, String routingKey, Object message,int delayTime) {
        GmallCorrelationData correlationData = new GmallCorrelationData();
        String correlationId = UUID.randomUUID().toString().replaceAll("-", "");
        //设置开启延迟队列,延迟队列的时间
        correlationData.setDelay(true);
        correlationData.setDelayTime(delayTime);//延迟的时间
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        correlationData.setMessage(message);
        correlationData.setId(correlationId);

        //将消息添加进缓存,设置10分钟过期时间
        redisTemplate.opsForValue().set(correlationId, JSON.toJSONString(correlationData),OBJECT_TIMEOUT, TimeUnit.MINUTES);
        //发送信息
        rabbitTemplate.convertAndSend(exchange, routingKey, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setDelay(delayTime*1000);//设置延迟时间
                return message;
            }
        },correlationData);
        return true;
    }

}