package com.suchengyu.gmall.mq.receive;

import com.suchengyu.gmall.mq.config.DeadLetterMqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DeadLetterReceiver
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-14
 * @Description:
 */
@Component
@Configuration
public class DeadLetterReceiver {

    @RabbitListener(queues = DeadLetterMqConfig.queue_dead_2)
    public void get(String msg){
        System.out.println("Receive:" + msg);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Receive queue_dead_2: "+ sdf.format(new Date()) + " Delay rece."+ msg);
    }

}

