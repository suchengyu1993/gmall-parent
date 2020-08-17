package com.suchengyu.gmall.test.juc_synchronized_title2;

/**
 * MyPhone
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-27
 * @Description:
 */
public class MyPhone {
    public  synchronized void show(){
        System.out.println("正在打电话!");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public  synchronized void send(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("正在发短信!");
    }
}
