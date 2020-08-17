package com.suchengyu.gmall.cart.test;

import java.math.BigDecimal;

/**
 * BigDecimalTest
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-07
 * @Description: 测试BigDecimal
 */
public class BigDecimalTest {
    public static void main(String[] args) {
        //1.分别在 BigDecimal中添加不同的值
        System.out.println(new BigDecimal(0.005F));
        System.out.println(new BigDecimal(0.005D));
        System.out.println(new BigDecimal("0.008"));
        System.out.println("-----------------------------------------------");

        //2.比较
//        int i = b.compareTo(a);
//        System.out.println(i);
        System.out.println("-----------------------------------------------");

        // 3. 运算
        BigDecimal b1 = new BigDecimal(0.05D);
        BigDecimal b2 = new BigDecimal(0.06D);
        System.out.println(b1.multiply(b2));
        System.out.println(b1.add(b2));

        BigDecimal s1 = new BigDecimal("0.05");
        BigDecimal s2 = new BigDecimal("0.06");
        System.out.println(s1.multiply(s2));
        System.out.println(s1.add(s2));
        System.out.println("-----------------------------------------------");

        // 4.除法会报错
        BigDecimal s6 = new BigDecimal("6");
        BigDecimal s7 = new BigDecimal("7");
//        System.out.println(s6.divide(s7));
        System.out.println("-----------------------------------------------");

        //5.使用约数
        BigDecimal divide = s6.divide(s7, 2, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(divide);

        BigDecimal a = new BigDecimal(0.01F);
        BigDecimal b = new BigDecimal(0.01D);
        BigDecimal add = a.add(b);
        BigDecimal bigDecimal = add.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        System.out.println(bigDecimal);

    }
}
