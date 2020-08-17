package com.suchengyu.gmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceProductApplication
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-13
 * @Description:
 */
@SpringBootApplication
@MapperScan({"com.suchengyu.gmall.product.mapper"})
@ComponentScan("com.suchengyu")
@EnableDiscoveryClient  //服务注册
@EnableFeignClients(basePackages = "com.suchengyu.gmall") //feign扫描调用
public class ServiceProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }
}
