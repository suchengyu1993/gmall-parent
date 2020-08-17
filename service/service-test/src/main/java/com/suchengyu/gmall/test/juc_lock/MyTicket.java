package com.suchengyu.gmall.test.juc_lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MyTicket
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-23
 * @Description:
 */
public class MyTicket {

    private Integer num = 51;

    Lock lock =  new ReentrantLock();

    //卖票方法
    public  long sale(){
        try {
            lock.lock();//上锁
            num -- ;
            printing();
        }finally {
            lock.unlock();//解锁
        }
        return num;
    }
    public synchronized void printing(){
        System.out.println(Thread.currentThread().getName() + "买走了一张票,目前剩余票数:" + num);
    }
}
