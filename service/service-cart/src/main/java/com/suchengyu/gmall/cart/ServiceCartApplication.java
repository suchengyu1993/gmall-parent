package com.suchengyu.gmall.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceCartApplication
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-05
 * @Description:
 */
@SpringBootApplication
@MapperScan({"com.suchengyu.gmall.cart.mapper"})
@ComponentScan("com.suchengyu.gmall")
@EnableDiscoveryClient  //服务注册
@EnableFeignClients(basePackages = "com.suchengyu.gmall") //feign扫描调用
public class ServiceCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCartApplication.class, args);
    }
}
