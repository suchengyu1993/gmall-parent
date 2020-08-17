package com.suchengyu.gmall.test.ReadWriteLockDemo;

/**
 * Test
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-30
 * @Description:
 */

public class Test {

    public static void main(String[] args) {
        MyScreen myScreen = new MyScreen();
        new Thread(()->{
            myScreen.write("体育老师说:下课");
        },"体育老师").start();
        new Thread(()->{
            myScreen.write("数字老师说:上课");
        },"数学老师").start();
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                Object read = myScreen.read();
                System.out.println("学生接收的内容:" + read);
            }).start();
        }
    }
}
