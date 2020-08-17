package com.suchengyu.gmall.tack.scheduled;

import com.alibaba.fastjson.JSON;
import com.suchengyu.gmall.common.constant.MqConst;
import com.suchengyu.gmall.common.entity.GmallCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ScheduledTask
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-12
 * @Description:
 */

@EnableScheduling //开启spring的定时工具注解
@Slf4j
@Component
public class ScheduledTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0/30 * * * * ?")//30 表示每分钟的第30秒执行一次  0/30 每隔30秒一次
    public void task(){
        System.out.println("每30秒执行一次");
        //从缓存中获取数据
        String msg = (String) redisTemplate.opsForList().rightPop(MqConst.MQ_KEY_PREFIX);
        if (StringUtils.isEmpty(msg)){
            return;
        }
        GmallCorrelationData  gmallCorrelationData = JSON.parseObject(msg, GmallCorrelationData.class);
        if (gmallCorrelationData.isDelay()){
            //再次发送消息
            // 此处不能调用RabbitService，因为不是首次发送，需要保留CorrelationData的原始数据，否则调用RabbitService发送会初始化retryCount，重新处理次数变为0
            rabbitTemplate.convertAndSend(gmallCorrelationData.getExchange(),
                    gmallCorrelationData.getRoutingKey(),gmallCorrelationData.getMessage(), new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            message.getMessageProperties().setDelay(gmallCorrelationData.getDelayTime()*1000);
                            return message;
                        }
                    },gmallCorrelationData);
        }else {
            rabbitTemplate.convertAndSend(gmallCorrelationData.getExchange(),
                    gmallCorrelationData.getRoutingKey(),gmallCorrelationData.getMessage(),gmallCorrelationData);
        }
    }

//    @Scheduled(cron = "0/5 * * * * ?")
//    public void a(){
//        System.out.println("输出测试");
//    }
}
