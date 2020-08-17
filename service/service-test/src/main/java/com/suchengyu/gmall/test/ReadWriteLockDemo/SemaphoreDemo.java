package com.suchengyu.gmall.test.ReadWriteLockDemo;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author xialei
 * 
 * 在信号量上我们定义两种操作：
 * acquire（获取） 当一个线程调用acquire操作时，它要么通过成功获取信号量（信号量减1），
 *             要么一直等下去，直到有线程释放信号量，或超时。
 * release（释放）实际上会将信号量的值加1，然后唤醒等待的线程。
 * 
 * 信号量主要用于两个目的，一个是用于多个共享资源的互斥使用，另一个用于并发线程数的控制。
 */
public class SemaphoreDemo {
  public static void main(String[] args){
      //初始化3个车位
      Semaphore semaphore = new Semaphore(3);
      //6辆车去停车
      for (int i = 0; i < 6; i++) {
          new Thread(()->{
              try {
                  semaphore.acquire();//获取信号灯
                  System.out.println(Thread.currentThread().getName() + "\t 抢到车位了");
                  TimeUnit.SECONDS.sleep(new Random().nextInt(5));//随机时间
                  System.out.println(Thread.currentThread().getName()+ "号车离开车位了-------");
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }finally {
                  semaphore.release();
              }
          },String.valueOf(i)).start();
      }
  }
}
 
