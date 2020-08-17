package com.suchengyu.gmall.test.juc_lock;

/**
 * Test
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-23
 * @Description:
 */
public class Test {

    public static void main(String[] args) {

        MyTicket myTicket = new MyTicket();
        for (int i = 0; i < 50; i++) {
            MyRunnableImpl myRunnable = new MyRunnableImpl(myTicket);
            new Thread(myRunnable).start();
        }

        //表达式缩写
//        new Thread(()->{
//            myTicket.sale();
//        }).start();
//        }
    }



}
