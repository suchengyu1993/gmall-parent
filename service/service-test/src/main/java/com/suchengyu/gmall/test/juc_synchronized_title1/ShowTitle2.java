package com.suchengyu.gmall.test.juc_synchronized_title1;

/**
 * ShowTitle1
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-24
 * @Description:
 */
public class ShowTitle2 implements Runnable{
    private PrintingTitle1 printingTitle1;

    public ShowTitle2(PrintingTitle1 printingTitle1){
        this.printingTitle1 = printingTitle1;
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            this.printingTitle1.pront1();
        }
    }
}
