package com.suchengyu.gmall.common.config;

/**
 * MQProducerAckConfig
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-10
 * @Description:
 */

import com.alibaba.fastjson.JSON;
import com.suchengyu.gmall.common.constant.MqConst;
import com.suchengyu.gmall.common.entity.GmallCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @Description 消息发送确认
 * <p>
 * ConfirmCallback  只确认消息是否正确到达 Exchange 中
 * ReturnCallback   消息没有正确到达队列时触发回调，如果正确到达队列不执行
 * <p>
 * 1. 如果消息没有到exchange,则confirm回调,ack=false
 * 2. 如果消息到达exchange,则confirm回调,ack=true
 * 3. exchange到queue成功,则不回调return
 * 4. exchange到queue失败,则回调return
 *
 */
@Component
@Slf4j
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);            //指定 ConfirmCallback
        rabbitTemplate.setReturnCallback(this);             //指定 ReturnCallback
    }

    /**
     * 确认消息是否发送成功
     * @param correlationData 数据的原始文本
     * @param ack   消息确认机制
     * @param cause 原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("进入消息确认机制");
        if (ack) {
            log.info("消息发送成功："+ JSON.toJSONString(correlationData));
            System.out.println("回调消息:消息发送成功");
        } else {
            log.info("消息发送失败："+ cause + "数据："+ JSON.toJSONString(correlationData));
            System.out.println("回调消息:消息发送失败");
            this.addRetry(correlationData);
        }
    }

    /**
     * 确认消息是否投递成功
     * @param message   信息
     * @param replyCode 回复码
     * @param replyText 回复信息
     * @param exchange  交换机
     * @param routingKey    路由key
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // 反序列化对象输出
        System.out.println("消息主体: "+ new String(message.getBody()));
        System.out.println("应答码: "+ replyCode);
        System.out.println("描述："+ replyText);
        System.out.println("消息使用的交换器 exchange : "+ exchange);
        System.out.println("消息使用的路由键 routing : "+ routingKey);
        // 如果投递失败，说明消息没有发出去，记录缓存，后期定时任务处理
        System.out.println("CorrelationId - >" + message.getMessageProperties().getHeaders().get("spring_returned_message_correlation"));
        String id = (String)message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        String gmallCorrelationDataStr = (String) redisTemplate.opsForValue().get(id);
        CorrelationData gmallCorrelationData = JSON.parseObject(gmallCorrelationDataStr, GmallCorrelationData.class);
        this.addRetry(gmallCorrelationData);
    }

    /**
     * 添加重试
     */
    public void addRetry(CorrelationData correlationData){

        GmallCorrelationData gmallCorrelationData = (GmallCorrelationData) correlationData;
        int retryCount = gmallCorrelationData.getRetryCount();//获取次数
        if (retryCount >= MqConst.RETRY_COUNT){// 次数 >= 3
            System.out.println("消息重发失败:" + JSON.toJSONString(gmallCorrelationData) + ",不在重发消息");
        }else {
            retryCount += 1;
            gmallCorrelationData.setRetryCount(retryCount);
            redisTemplate.opsForList().leftPush(MqConst.MQ_KEY_PREFIX, JSON.toJSONString(gmallCorrelationData));
            redisTemplate.opsForValue().set(gmallCorrelationData.getId(), JSON.toJSONString(gmallCorrelationData),30,TimeUnit.MINUTES);
        }
    }

}