package com.suchengyu.gmall.payment.receiver;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;


/**
 * PaymentCanelMqConfig
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-17
 * @Description:
 */
@Configuration
public class PaymentDelayMqConfig {

    public static final String exchange_delay_payment_check = "exchange.delay.payment.check";
    public static final String routing_delay_check = "routing.delay.check";
    public static final String queue_delay_payment_check= "queue.delay.payment.check";

    //创建队列
    @Bean
    public Queue paymentQueue(){
        // 第一个参数是创建的queue的名字，第二个参数是是否支持持久化
        return new Queue(queue_delay_payment_check,true);
    }

    //创建交换机
    @Bean
    public CustomExchange paymentExchange(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-delayed-type", "direct");
        return new CustomExchange(exchange_delay_payment_check, "x-delayed-message",true,false,map);
    }

    //绑定
    @Bean
    public Binding paymentBinding(){
        return BindingBuilder.bind(paymentQueue()).to(paymentExchange()).with(routing_delay_check).noargs();
    }
}
