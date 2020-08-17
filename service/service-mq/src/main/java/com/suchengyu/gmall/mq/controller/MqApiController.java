package com.suchengyu.gmall.mq.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.common.service.RabbitService;
import com.suchengyu.gmall.mq.config.DeadLetterMqConfig;
import com.suchengyu.gmall.mq.config.DelayedMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MqApiController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-10
 * @Description:消息发送端
 */
@RestController
@RequestMapping("/mq")
@Slf4j
public class MqApiController {

    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 调用消息队列api发送一个普通消息
     */
    @GetMapping("/sendConfirm")
    public Result sendConfirm(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rabbitService.sendMessage("exchange.confirm", "routing.confirm1",sdf.format(new Date())+ "消息已发送");
        System.out.println("消息发送端:消息发送成功");
        return Result.ok();
    }

    /**
     * 死信队列的方式一,单独设置
     */
    @RequestMapping("/sendDeadLettler")
    public Result sendDeadLettler(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //方式一：指定每个消息延迟时间，存在问题：后进入队列会阻塞先进入队列超时消息，原因：先进先出，即使超时也出不来
        rabbitTemplate.convertAndSend(DeadLetterMqConfig.exchange_dead, DeadLetterMqConfig.routing_dead_1, sdf.format(new Date()) + ",消息发送", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(1*1000*10 + "");
                System.out.println(sdf.format(new Date()) + " Delay sent.");
                return message;
            }
        });
        return Result.ok("访问死信队列成功");
    }

    /**
     *死信队列的方式二,统一设置
     */
    @RequestMapping("sendDeadLettle")
    public Result sendDeadLettle(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rabbitTemplate.convertAndSend(DeadLetterMqConfig.exchange_dead, DeadLetterMqConfig.routing_dead_1,"发送的消息111111");
        System.out.println(sdf.format(new Date()) + " Delay sent.");
        return Result.ok();
    }

    /**
     * 延迟队列发送消息
     */
    @RequestMapping(value = "/sendDelay")
    public Result sendDelay(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rabbitTemplate.convertAndSend(DelayedMqConfig.exchange_delay, DelayedMqConfig.routing_delay, sdf.format(new Date())+"消息发送成功", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置消息的延迟时间
                message.getMessageProperties().setDelay(10*1000);
                System.out.println(sdf.format(new Date())+"Delay send !!!!!!!!!!!!!! ");
                return message;
            }
        });
        return Result.ok("延迟队列访问成功");
    }

}
