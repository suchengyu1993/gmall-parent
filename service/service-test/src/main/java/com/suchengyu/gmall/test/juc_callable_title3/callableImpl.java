package com.suchengyu.gmall.test.juc_callable_title3;

import java.util.concurrent.Callable;

/**
 * callableImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-24
 * @Description:
 */
public class callableImpl implements Callable<Double> {
    public String call = "";
    public callableImpl(String call ){
        this.call=call;
    }
    @Override
    public Double call() throws Exception {
        Double price = 0.0;

        if (call.equals("jd")){
            System.out.println("正在查询京东价格....");
            price = 130D;
        }else if (call.equals("tb")){
            System.out.println("正在查询淘宝价格....");
            price = 100D;
        }else {
            System.out.println("正在查询拼多多价格....");
            price = 20D;
            Thread.sleep(2000);
        }
        return price;
    }
}
