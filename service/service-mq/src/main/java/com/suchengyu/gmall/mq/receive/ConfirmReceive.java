package com.suchengyu.gmall.mq.receive;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * ConfirmReceive
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-10
 * @Description:消息接收端(listener监听器)
 */
@Component
@Configuration
public class ConfirmReceive {

    /**
     * 消费端recerver(listener监听器)
     * 核心就是将队列绑定到交换机上,通过路由值routingkey
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue.confirm",autoDelete = "false"),
            exchange = @Exchange(value = "exchange.confirm",autoDelete = "false"),
            key = {"routing.confirm"}))
    public void process(Message message, Channel channel){
        try{
            System.out.println("进入消息消费队列:消费exchange.confirm交换机上的消息队列");
//            System.out.println("出现了异常");
//            int i  = 12 / 0;
            System.out.println(message);
            long deliveryTag = message.getMessageProperties().getDeliveryTag();//获取投放标签
            channel.basicAck(deliveryTag, false);//确认收到消息,false表示是否批量确认
        }catch (Exception e){
            //判断消息是否被重新投递过
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            Boolean redelivered = message.getMessageProperties().getRedelivered();
//            System.out.println("消息重发过了? == " + redelivered);
            if (redelivered){
                System.out.println("消息已经被重发过,不在重发");
            }else{
                System.out.println("消息没有被重发,重发一次");
                //确认未收到消息,false 是否批量确认, true 是否重新投递
                channel.basicNack(deliveryTag, false, true);
            }
        }
        System.out.println("RabbitListener:" + new String(message.getBody()) + "!!!消息消费结束");
    }
}
