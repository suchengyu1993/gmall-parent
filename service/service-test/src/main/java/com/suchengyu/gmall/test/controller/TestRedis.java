package com.suchengyu.gmall.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * TestRedis
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-23
 * @Description:
 */
@RestController
public class TestRedis {
    @Autowired
    private RedisTemplate redisTemplate;

    //测试redis缓存中使用分布式锁
    @GetMapping("/testLockNx")
    public String testLock(){
        System.out.println("正在请求微服务");
        //设置分布式锁
        Boolean stcokLock = redisTemplate.opsForValue().setIfAbsent("stcokLock", 1, 3, TimeUnit.SECONDS);//3秒钟分布式锁过期
        //判断是否获取分布式锁的key,拿到锁就可以操作数据库(缓存)
        if (stcokLock){
            //获取缓存中的值(这边也可以是查数据库)
            String stock = redisTemplate.opsForValue().get("stock").toString();
            int i = Integer.parseInt(stock);
            if (i>0){
                i--;
                //算术运算后,设置剩余数
                redisTemplate.opsForValue().set("stock", i);
                System.out.println("商品目前剩余数量:"+i);
            }else {
                System.out.println("商品已抢完");
            }
            redisTemplate.delete("stcokLock");//操作完成后,释放分布式锁
        }else{
            //没有分布式锁,等待,进入自旋
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return testLock();//自旋
        }
        return "快快抢购吧";
    }
}
