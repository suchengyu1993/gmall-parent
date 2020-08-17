package com.suchengyu.gmall.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * TestController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-21
 * @Description:
 */
@RestController
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;

    //使用分布式锁
    @GetMapping("/testLockNx")
    public String testLockNx(){
        System.out.println("正在请求分布式节点中的一个微服务");
        //设置分布式锁
        String uid = UUID.randomUUID().toString();
        Boolean stockLock = redisTemplate.opsForValue().setIfAbsent("stockLock", uid, 50, TimeUnit.SECONDS);//3秒钟分布式锁过期
        //判断是否获取分布式锁的key
        if (stockLock) {
            String stock = redisTemplate.opsForValue().get("stock").toString();
            int i = Integer.parseInt(stock);
            if (i > 0) {
                i--; //算术运算后设置剩余数
                redisTemplate.opsForValue().set("stock", i);
                System.out.println("商品目前剩余数量:" + i);
            } else {
                System.out.println("商品已卖完");
            }
            // lua脚本防误删
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            // 设置lua脚本返回的数据类型
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            // 设置lua脚本返回类型为Long
            redisScript.setResultType(Long.class);
            redisScript.setScriptText(script);
            redisTemplate.execute(redisScript, Arrays.asList("stockLock"),uid);

//            //删除锁之前判断一下删除的是否是自己当前的锁
//            String delUid = (String)redisTemplate.opsForValue().get("stockLock");
//            if (uid.equals(delUid)){
//                redisTemplate.delete("stockLock");      //执行完成后,释放分布式锁
//            }
        }else {
            //没有分布式锁,需要等待,然后自旋
            try {
                Thread.sleep(2000);
                System.out.println("没拿到锁,正在进入自旋");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return testLockNx(); //自旋
        }
        return "拿出你的手速抢爆它";
    }


    //测试并发请求redis
//    @GetMapping("/testLock")
//    public String testLock(){
//        System.out.println("正在请求分布式节点中的一个微服务");
//        //获取缓存中的值
//        String stock = redisTemplate.opsForValue().get("stock").toString();
//        int i = Integer.parseInt(stock);
//        if (i > 0){
//            i--;
//            //算术运算后设置剩余数
//            redisTemplate.opsForValue().set("stock", i);
//            System.out.println("商品目前剩余数量:"+i);
//        }else {
//            System.out.println("商品已卖完");
//        }
//        return "拿出你的手速抢爆它";
//    }



}
