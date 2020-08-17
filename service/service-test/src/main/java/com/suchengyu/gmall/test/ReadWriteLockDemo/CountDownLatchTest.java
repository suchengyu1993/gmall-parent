package com.suchengyu.gmall.test.ReadWriteLockDemo;

import java.util.concurrent.CountDownLatch;

/**
 * CountDownLatchTest
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-30
 * @Description:减少计数器
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 0; i < 6; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + "/t 号同学离开教室");
                countDownLatch.countDown();//每走一个同学就减一
            }).start();
        }
        countDownLatch.await();//阻塞,等减到0才执行
        System.out.println(Thread.currentThread().getName() + "\t****** 班长关门走人，main线程是班长");
    }
}
