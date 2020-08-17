package com.suchengyu.gmall.test.juc_Async;

import com.suchengyu.gmall.model.product.BaseCategory1;
import com.suchengyu.gmall.model.product.SkuInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * AsyncTest
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-29
 * @Description:
 */
public class AsyncTest {

    //项目分析
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //查询skuInfo线程,要有返回值
        CompletableFuture<SkuInfo> skuInfoCompletableFuture= CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                System.out.println("查询skuInfo的线程开始了");
                SkuInfo skuInfo = new SkuInfo();
                skuInfo.setSkuName("联想Y7000P");
                skuInfo.setCategory3Id(61L);
                return skuInfo;
            }
        });
        //查询价格线程,要有返回值,跟skuInfo线程要异步
        CompletableFuture<Double> priceCompletableFuture = CompletableFuture.supplyAsync(new Supplier<Double>() {
            @Override
            public Double get() {
                System.out.println("查询价格线程开始了!!!");
                return 100.0;
            }
        });
        //查询分类信息,依赖skuInfo中的3级分类Id
        CompletableFuture<BaseCategory1> category1CompletableFuture = skuInfoCompletableFuture.thenApplyAsync(new Function<SkuInfo, BaseCategory1>() {
            @Override
            public BaseCategory1 apply(SkuInfo skuInfo) {
                System.out.println("查询分类线程开始了!!");
                BaseCategory1 baseCategory1 = new BaseCategory1();
                baseCategory1.setId(skuInfo.getCategory3Id());
                return baseCategory1;
            }
        });
        CompletableFuture.anyOf(skuInfoCompletableFuture).join();
        SkuInfo skuInfo = skuInfoCompletableFuture.get();
        Double aDouble = priceCompletableFuture.get();
        BaseCategory1 baseCategory1 = category1CompletableFuture.get();

        System.out.println("skuInfo:" + skuInfo.getSkuName());
        System.out.println("price:" + aDouble);
        System.out.println("baseCategory1:" + baseCategory1.getId());


    }



    //不使用自由变量
    public static void b() {
        CompletableFuture<Long> completableFuture =CompletableFuture.supplyAsync(new Supplier<Long>() {
            @Override
            public Long get() {
                System.out.println("级联写法,线程开启了");
                int i = 2/0;
                return 1024L;
            }
        }).exceptionally(new Function<Throwable, Long>() {
            @Override
            public Long apply(Throwable throwable) {
                System.out.println("出现了异常,运算值修改了");
                return 2048L;
            }
        }).whenComplete(new BiConsumer<Long, Throwable>() {
            @Override
            public void accept(Long aLong, Throwable throwable) {
                System.out.println("对结果进行处理");
                System.out.println("whenComplete:" + aLong);
            }
        });
        Long aLong = null;
        try {
            aLong = completableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("最终返回的结果是:" + aLong);
    }


    //用自由变量
    public static void a() throws ExecutionException, InterruptedException {
        //创建有返回结果的线程
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(new Supplier<Long>() {
            @Override
            public Long get() {
                System.out.println("线程开始执行了");
                int i = 12/0;
                return 1024L;
            }
        });
        completableFuture.exceptionally(new Function<Throwable, Long>() {
            @Override
            public Long apply(Throwable throwable) {
                System.out.println("线程出现异常.结果有改变");
                return 2024L;
            }
        });
        //对线程的返回结果处理
        completableFuture.whenComplete(new BiConsumer<Long, Throwable>() {
            @Override
            public void accept(Long aLong, Throwable throwable) {
                System.out.println("对结果进行处理");
                System.out.println("whenComplete:" + aLong);
            }
        });
        Long aLong = completableFuture.get();
        System.out.println("获取的返回值为:" + aLong);
    }

}
