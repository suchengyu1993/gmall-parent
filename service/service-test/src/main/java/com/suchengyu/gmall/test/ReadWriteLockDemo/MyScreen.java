package com.suchengyu.gmall.test.ReadWriteLockDemo;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * MyScreen
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-30
 * @Description:读写锁的资源
 */
public class MyScreen {

    String str = new String();
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();//创建读写锁
    //写
    public void write(String write){
        //获取读锁
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();//上锁
        try {
            System.out.println(Thread.currentThread().getName() + "正在执行写入操作");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            str = write;
        }finally {
            writeLock.unlock();//解锁
        }
    }

    //读
    public String read(){
        //获取读锁,使用同一把锁
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            System.out.println(Thread.currentThread().getName() + "正在执行读操作");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return str;
        }finally {
            readLock.unlock();
        }
    }
}
