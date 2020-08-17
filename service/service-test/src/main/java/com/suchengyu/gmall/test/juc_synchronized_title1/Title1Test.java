package com.suchengyu.gmall.test.juc_synchronized_title1;

/**
 * Title1Test
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-24
 * @Description:
 */

public class Title1Test {

    public static void main(String[] args) {
        /**
         * 需求:一道线程通讯的面试题：
         * 两个线程共同完成一个任务，打印0和1，打印50遍，打印结果
         * 0 1 0 1 0 1 0 1 0 1 0 1
         */

//        // 1.常规方式
//        PrintingTitle1 printingTitle1 = new PrintingTitle1();
//        ShowTitle2 showTitle2 = new ShowTitle2(printingTitle1);
//        Thread thread = new Thread(showTitle2);
//        thread.start();
//
//        ShowTitle1 showTitle1 = new ShowTitle1(printingTitle1);
//        Thread thread1 = new Thread(showTitle1);
//        thread1.start();

        // 2. 简写方式
        PrintingTitle1 printingTitle1 = new PrintingTitle1();
        new Thread(()->{
            for (int i = 0; i < 50; i++) {
                printingTitle1.print0();
            }
        },"打印0的线程1").start();

        new Thread(()->{
            for (int i = 0; i < 50; i++) {
                printingTitle1.print0();
            }
        },"打印0的线程2").start();

        new Thread(()->{
            for (int i = 0; i < 50; i++) {
                printingTitle1.pront1();
            }
        },"打印1的线程1").start();

        new Thread(()->{
            for (int i = 0; i < 50; i++) {
                printingTitle1.pront1();
            }
        },"打印1的线程2").start();

    }
}
