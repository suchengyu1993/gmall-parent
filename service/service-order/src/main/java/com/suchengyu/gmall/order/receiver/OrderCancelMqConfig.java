package com.suchengyu.gmall.order.receiver;

import com.suchengyu.gmall.common.constant.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;


/**
 * OrderCanelMqConfig
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-15
 * @Description:
 */
@Configuration
public class OrderCancelMqConfig {

    @Bean
    public Queue delayQueue(){
        // 第一个参数是创建的queue的名字，第二个参数是是否支持持久化
        return new Queue(MqConst.QUEUE_ORDER_CANCEL,true);
    }

    @Bean
    public CustomExchange delayExchange(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("x-delayed-type", "direct");
        // 交换机名, 交换机类型, 是否持久化,是否自动删除,
        return new CustomExchange(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL,
                "x-delayed-message",true,false,map);

    }

    @Bean
    public Binding bindingDelay(){
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(MqConst.ROUTING_ORDER_CANCEL).noargs();
    }

}
