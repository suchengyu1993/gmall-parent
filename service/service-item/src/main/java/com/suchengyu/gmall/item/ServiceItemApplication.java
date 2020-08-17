package com.suchengyu.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceItemApplication
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-18
 * @Description:
 */
@SpringBootApplication
@ComponentScan({"com.suchengyu.gmall"})
@EnableDiscoveryClient  //服务注册
@EnableFeignClients(basePackages = "com.suchengyu.gmall") //feign扫描调用
public class ServiceItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class, args);
    }
}
