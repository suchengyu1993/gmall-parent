package com.suchengyu.gmall.test.juc;

/**
 * MyTicket
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-23
 * @Description:
 */
public class MyTicket {

    private Integer num = 101;

    //卖票方法
    //必须确保多个线程使用同一把锁
    public synchronized long sale(){
        num -- ;
        printing();
        return num;
    }
    public synchronized void printing(){
        System.out.println(Thread.currentThread().getName() + "买走了一张票,目前剩余票数:" + num);
    }
}
