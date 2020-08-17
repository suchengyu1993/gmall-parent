package com.suchengyu.gmall.test.juc_lock_title2;

/**
 * test
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-24
 * @Description:
 */
public class test {
    public static void main(String[] args) {
        /**
         * 面试题：有一个餐馆，有三位厨师，三位厨师分别负责切菜，炒菜，端菜。
         * 某一天来了10个客人，分别点了10道菜，用三条线程代表三个厨师，去完成三道菜的烹饪？
         */
        FoodRoom foodRoom = new FoodRoom();
        new Thread(()->{
            for (int i = 0; i < 20; i++) {
                foodRoom.cut();
            }
        },"切菜大师,").start();

        new Thread(()->{
            for (int i = 0; i < 20; i++) {
                foodRoom.cook();
            }
        },"炒菜大师,").start();

        new Thread(()->{
            for (int i = 0; i < 20; i++) {
                foodRoom.give();
            }
        },"端菜大师,").start();
    }

}
