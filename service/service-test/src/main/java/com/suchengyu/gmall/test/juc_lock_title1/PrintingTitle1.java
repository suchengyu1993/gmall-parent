package com.suchengyu.gmall.test.juc_lock_title1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PrintingTitle1
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-24
 * @Description:
 */
public class PrintingTitle1 {
    private int num = 1;
    Lock lock =  new ReentrantLock();//创建可重入锁
    Condition condition0 =lock.newCondition();
    Condition condition1 =lock.newCondition();

    public  void print0() {
        lock.lock();//上锁
        try {
            while (num != 1) {
                try {
                    condition0.await();//阻塞线程
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            num--;
            System.out.println(Thread.currentThread().getName() + "执行方法0,打印:" + num);
            condition1.signal();//唤醒其他线程
        }finally {
            lock.unlock();//解锁
        }
    }

    public  void pront1() {
        lock.lock();
        try {
            while (num != 0) {
                try {
                   condition1.await();//阻塞线程
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            num++;
            System.out.println(Thread.currentThread().getName() + "执行方法1,打印:" + num);
            condition0.signal();//唤醒其他线程
        }finally {
            lock.unlock();
        }
    }
}
