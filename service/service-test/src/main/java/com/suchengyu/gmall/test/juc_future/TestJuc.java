package com.suchengyu.gmall.test.juc_future;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * TestJuc
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-25
 * @Description:
 */
public class TestJuc {
    public static void main(String[] args) {
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(new Supplier<Long>() {
            @Override
            public Long get() {
                return null;
            }
        });
    }
}
