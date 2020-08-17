package com.suchengyu.gmall.test.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * test
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-30
 * @Description:
 */
public class test {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
       list.add("1");
        Vector<String> strings = new Vector<>();
        strings.add("2");
        List<String> list2 = new CopyOnWriteArrayList<>();
        list2.add("4");

    }
}
