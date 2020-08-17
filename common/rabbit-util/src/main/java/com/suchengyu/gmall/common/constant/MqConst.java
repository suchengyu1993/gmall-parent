package com.suchengyu.gmall.common.constant;

/**
 * MqConst
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-12
 * @Description:消息补偿
 */
public class MqConst {

    public static final String MQ_KEY_PREFIX = "mq:list";
    public static final int RETRY_COUNT = 3 ;

    /**
     * 商品上下架
     */
    public static final String EXCHANGE_DIRECT_GOODS = "exchange.direct.goods";
    public static final String ROUTING_GOODS_UPPER = "goods.upper";
    public static final String ROUTING_GOODS_LOWER = "goods.lower";
    //队列
    public static final String QUEUE_GOODS_UPPER  = "queue.goods.upper";
    public static final String QUEUE_GOODS_LOWER  = "queue.goods.lower";

    /**
     * 取消订单，发送延迟队列
     */
    public static final String EXCHANGE_DIRECT_ORDER_CANCEL = "exchange.direct.order.cancel";
    public static final String ROUTING_ORDER_CANCEL = "order.create";
    //延迟取消订单队列
    public static final String QUEUE_ORDER_CANCEL  = "queue.order.cancel";
    //取消订单 延迟时间 单位：秒
    public static final int DELAY_TIME  = 2*60;

    /**
     * 订单支付
     */
    public static final String EXCHANGE_DIRECT_PAYMENT_PAY = "exchange.direct.payment.pay";
    public static final String ROUTING_PAYMENT_PAY = "payment.pay";
    //队列
    public static final String QUEUE_PAYMENT_PAY  = "queue.payment.pay";
    /**
     * 减库存
     */
    public static final String EXCHANGE_DIRECT_WARE_STOCK = "exchange.direct.ware.stock";
    public static final String ROUTING_WARE_STOCK = "ware.stock";
    //队列
    public static final String QUEUE_WARE_STOCK  = "queue.ware.stock";
    /**
     * 减库存成功，更新订单状态
     */
    public static final String EXCHANGE_DIRECT_WARE_ORDER = "exchange.direct.ware.order";
    public static final String ROUTING_WARE_ORDER = "ware.order";
    //队列
    public static final String QUEUE_WARE_ORDER  = "queue.ware.order";

    /**
     * 关闭交易
     */
    public static final String EXCHANGE_DIRECT_PAYMENT_CLOSE = "exchange.direct.payment.close";
    public static final String ROUTING_PAYMENT_CLOSE = "payment.close";
    //队列
    public static final String QUEUE_PAYMENT_CLOSE  = "queue.payment.close";

}
