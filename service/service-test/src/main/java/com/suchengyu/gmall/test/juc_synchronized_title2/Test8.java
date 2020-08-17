package com.suchengyu.gmall.test.juc_synchronized_title2;

/**
 * Test8
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-27
 * @Description:Syn的8中情况(
 */
public class Test8 {

    public static void main(String[] args) throws InterruptedException {
        //1 同一个内存对象的两个syn方法是否阻塞?   是
        //2 不同内存对象的两个syn方法是否阻塞?     否
        //3 同一个内存对象的syn和static syn方法是否阻塞？  否
        //4 不同内存对象的static syn和syn方法是否阻塞？    否
        //5 不同内存对象的两个static syn方法是否阻塞？     是
        //6 同一个内存对象的两个static syn方法是否阻塞？   是
        //7 同一个对象的syn和普通方法是否阻塞?             否
        //8 不同对象的syn和普通方法是否阻塞?               否
        MyPhone myPhone = new MyPhone();
        MyPhone myPhone2 = new MyPhone();
        new Thread(()->{
            myPhone.show();
        }).start();
        Thread.sleep(1000);
        new Thread(()->{
            myPhone.send();
        }).start();

    }
}
