package com.suchengyu.gmall.test.juc_callable_title3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Test
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-24
 * @Description:
 */
public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //同一个线程操作，两个任务同时处理，处理两次
        callableImpl callableImpljd = new callableImpl("jd");
        FutureTask<Double> jd = new FutureTask<>(callableImpljd);
        new Thread(jd).start();
        callableImpl callableImpltb = new callableImpl("tb");
        FutureTask<Double> tb = new FutureTask<>(callableImpltb);
        new Thread(tb).start();
        callableImpl callableImplpdd = new callableImpl("pdd");
        FutureTask<Double> pdd = new FutureTask<>(callableImplpdd);
        new Thread(pdd).start();
        Double jdPrice = jd.get();
        Double tbPrice = tb.get();
        Double pddPrice = pdd.get();
        System.out.println("阻塞主线程");
        System.out.println("该商品京东价格为："+jdPrice);
        System.out.println("该商品淘宝价格为："+tbPrice);
        System.out.println("该商品拼多多价格为："+pddPrice);
        System.out.println("主线程执行完毕");
    }
}
