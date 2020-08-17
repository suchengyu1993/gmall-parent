package com.suchengyu.gmall.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * TestAppliaction
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-21
 * @Description:
 */
@EnableDiscoveryClient
@ComponentScan({"com.suchengyu.gmall"})
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class TestAppliaction {
    public static void main(String[] args) {
        SpringApplication.run(TestAppliaction.class, args);
    }
}
