package com.suchengyu.gmall.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * DeadletterMqConfig
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-13
 * @Description:死信队列的配置文件
 */
@Configuration
public class DeadLetterMqConfig {

    public static final String exchange_dead = "exchange.dead";
    public static final String routing_dead_1 = "routing.dead.1";
    public static final String routing_dead_2 = "routing.dead.2";
    public static final String queue_dead_1 = "queue.dead.1";
    public static final String queue_dead_2 = "queue.dead.2";

    /**
     * 创建一个普通的交换机
     */
    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(exchange_dead, true, false,null);
    }

    /**
     *创建队列一
     */
    @Bean
    public Queue queue1(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", exchange_dead);
        map.put("x-dead-letter-routing-key", routing_dead_2);
        //方式二,统一延迟时间
        map.put("x-message-ttl", 10*1000);
        //方式一与方式二切换时，必须先删除对应交换机与队列，否则出错
        return new Queue(queue_dead_1,true,false,false,map);
    }

    /**
     * 队列一绑定交换机跟路由key
     */
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue1()).to(exchange()).with(routing_dead_1);
    }

    /**
     * 队列二,死信队列
     */
    @Bean
    public Queue queue2(){
        return new Queue(queue_dead_2,true,false,false,null);
    }

    /**
     *队列二绑定交换机跟路由key
     */
    @Bean
    public Binding deadBinding(){
        return BindingBuilder.bind(queue2()).to(exchange()).with(routing_dead_2);
    }

}
