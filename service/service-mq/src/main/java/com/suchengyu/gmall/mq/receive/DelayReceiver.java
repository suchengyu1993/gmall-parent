package com.suchengyu.gmall.mq.receive;

import com.suchengyu.gmall.mq.config.DelayedMqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DelayReceiver
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-15
 * @Description:
 */
@Component
@Configuration
public class DelayReceiver {


    @RabbitListener(queues = DelayedMqConfig.queue_delay_1)
    public void getDelayMessage(String msg){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("监视延迟队列,信息已进入到延迟队列了" + sdf.format(new Date()) + "!!! Delay rece." + msg);
    }
}
