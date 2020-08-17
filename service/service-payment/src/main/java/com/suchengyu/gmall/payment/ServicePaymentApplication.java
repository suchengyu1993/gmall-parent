package com.suchengyu.gmall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServicePaymentApplication
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-08
 * @Description:
 */
@SpringBootApplication
@ComponentScan("com.suchengyu.gmall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.suchengyu.gmall")
public class ServicePaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePaymentApplication.class, args);
    }

}
