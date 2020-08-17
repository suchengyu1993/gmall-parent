package com.suchengyu.gmall.test.juc;

/**
 * MyRunnableImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-23
 * @Description:
 */
public class MyRunnableImpl implements Runnable {

    MyTicket myTicket;
    public MyRunnableImpl( MyTicket myTicket){
        this.myTicket = myTicket;
    }

    @Override
    public void run() {
        this.myTicket.sale();
    }
}
