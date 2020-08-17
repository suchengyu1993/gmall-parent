package com.suchengyu.gmall.test.juc_lock_title2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FoodRoom
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-24
 * @Description:
 */
public class FoodRoom {

    private int num = 1;//1 切菜  2 炒菜    3 端菜
    Lock lock =  new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();

    public void cut(){
        lock.lock();//加锁
        try {
            while (num != 1){
                try {
                    condition1.await();//休眠
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + "展示刀工了------");
            num = 2;
            condition2.signal();//唤醒2号线程
        }finally {
          lock.unlock();//解锁
        }
    }

    public void cook(){
        lock.lock();
        try {
            while (num!=2){
                try {
                    condition2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName()+ "掌控火候表演========");
            num = 3;
            condition3.signal();//唤醒3号线程
        }finally {
            lock.unlock();
        }
    }

    public void give(){
        lock.lock();
        try {
            while(num != 3){
                try {
                    condition3.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + "摆好拼盘了+++++++++++++");
            num = 1;
            condition1.signal();//唤醒1号线程
        }finally {
            lock.unlock();
        }
    }
}
