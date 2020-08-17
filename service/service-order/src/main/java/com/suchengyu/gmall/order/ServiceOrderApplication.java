package com.suchengyu.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceOrderApplication
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-07
 * @Description:
 */
@SpringBootApplication
@ComponentScan("com.suchengyu.gmall")
@EnableDiscoveryClient //服务发现
@EnableFeignClients(basePackages ="com.suchengyu.gmall" ) //feign扫描调用
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }
}
