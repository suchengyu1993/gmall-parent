package com.suchengyu.gmall.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * ServiceMqApplication
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-10
 * @Description:
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"com.suchengyu.gmall"})
@EnableDiscoveryClient
public class ServiceMqApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceMqApplication.class, args);
    }
}
