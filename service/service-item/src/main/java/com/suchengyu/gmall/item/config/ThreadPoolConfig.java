package com.suchengyu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolConfig
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-30
 * @Description:自定义线程
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 100, 50, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
        return threadPoolExecutor;
    }
}
