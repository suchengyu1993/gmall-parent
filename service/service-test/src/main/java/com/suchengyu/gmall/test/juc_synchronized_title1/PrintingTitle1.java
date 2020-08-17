package com.suchengyu.gmall.test.juc_synchronized_title1;

/**
 * PrintingTitle1
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-24
 * @Description:
 */
public class PrintingTitle1 {
    private int num = 1;

    public synchronized void print0() {
        while (num != 1) {
            try {
                this.wait();//阻塞线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num--;
        System.out.println(Thread.currentThread().getName() + "执行方法0,打印:" + num);
        notifyAll();//唤醒全部线程
    }

    public synchronized void pront1() {
        while (num != 0) {
            try {
                this.wait();//阻塞线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num++;
        System.out.println(Thread.currentThread().getName() + "执行方法1,打印:" + num);
        notifyAll();//唤醒全部线程
    }
}
