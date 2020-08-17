package com.suchengyu.gmall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.suchengyu.gmall.common.constant.MqConst;
import com.suchengyu.gmall.common.constant.RedisConst;
import com.suchengyu.gmall.common.service.RabbitService;
import com.suchengyu.gmall.common.util.HttpClientUtil;
import com.suchengyu.gmall.model.enums.OrderStatus;
import com.suchengyu.gmall.model.enums.ProcessStatus;
import com.suchengyu.gmall.model.order.OrderDetail;
import com.suchengyu.gmall.model.order.OrderInfo;
import com.suchengyu.gmall.order.mapper.OrderDetailMapper;
import com.suchengyu.gmall.order.mapper.OrderInfoMapper;
import com.suchengyu.gmall.order.service.OrderApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * OrderApiServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-10
 * @Description:
 */
@Service
public class OrderApiServiceImpl implements OrderApiService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${ware.url}")
    private String WARE_URL;
    @Autowired
    private RabbitService rabbitService;


    //支付成功,通知仓库减库存
    public void sendOrderStatus(Long orderId) {
        //通知仓库,修改订单状态
        this.updateOrderStatus(orderId, ProcessStatus.WAITING_DELEVER);
        String wareJson =  this.initWareOrder(orderId);
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_WARE_STOCK, MqConst.ROUTING_WARE_STOCK, wareJson);
    }

    /**
     * 根据orderId获取json字符串
     * @param orderId
     */
    private String initWareOrder(Long orderId) {
        OrderInfo orderInfo = this.getOrderInfo(orderId);
        //将orderInfo中部分数据转换为Map
        Map map = this.initWareOrder(orderInfo);
        return JSON.toJSONString(map);
    }

    /**
     * 将orderInfo中部分数据转换为Map
     * @param orderInfo
     * @return
     */
    public Map initWareOrder(OrderInfo orderInfo){
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderId", orderInfo.getId());
        map.put("consignee",orderInfo.getConsignee() );//收货人
        map.put("consigneeTel",orderInfo.getConsigneeTel() );//收件人电话
        map.put("orderComment",orderInfo.getOrderComment() );//订单备注
        map.put("orderBody",orderInfo.getTradeBody() );//订单描述
        map.put("deliveryAddress",orderInfo.getDeliveryAddress() );//送货地址
        map.put("paymentWay","2" );//付款方式
        map.put("wareId", orderInfo.getWareId());//仓库Id ，减库存拆单时需要使用！
        /*
        details:[{skuId:101,skuNum:1,skuName:
        ’小米手64G’},
        {skuId:201,skuNum:1,skuName:’索尼耳机’}]
     */
        ArrayList<Map> mapArrayList = new ArrayList<>();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            HashMap<String, Object> orderDetailMap = new HashMap<>();
            orderDetailMap.put("skuId", orderDetail.getSkuId());
            orderDetailMap.put("skuNum", orderDetail.getSkuNum());
            orderDetailMap.put("skuName", orderDetail.getSkuName());
            mapArrayList.add(orderDetailMap);
        }
        map.put("details", mapArrayList);
        return map;
    }

    //处理过期订单
    public void execExpiredOrder(Long orderId) {
        updateOrderStatus(orderId, ProcessStatus.CLOSED);
    }

    //根据订单id,修改订单的状态
    public void updateOrderStatus(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setProcessStatus(processStatus.name());
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        orderInfoMapper.updateById(orderInfo);
    }

    //提交订单,跳转到支付页面
    public OrderInfo getOrderInfo(long orderId) {
        //查询订单信息
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        //查询订单详情
        QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(wrapper);
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }

    //验证库存
    public boolean checkStock(Long skuId, Integer skuNum) {
        String result = HttpClientUtil.doGet(WARE_URL + "/hasStock?skuId=" + skuId + "&num=" + skuNum);
        return "1".equals(result);
    }

    //比较交易码
    public boolean checkTradeCode(String userId, String tradeNo) {
        //定义key
        String tradeNoKey = RedisConst.USER_KEY_PREFIX + userId + ":tradeNo";
        String redisTradeNo = (String) redisTemplate.opsForValue().get(tradeNoKey);
        if (StringUtils.isEmpty(redisTradeNo)){
            return false;
        }else{
           return redisTradeNo.equals(tradeNo);
        }
    }

    //删除交易码
    public void deleteTradeNo(String userId) {
        //定义key
        String tradeNoKey = RedisConst.USER_KEY_PREFIX + userId + ":tradeNo";
        redisTemplate.delete(tradeNoKey);
    }


    //生成交易码
    public String getTradeNo(String userId) {
        //定义key
        String tradeNoKey = RedisConst.USER_KEY_PREFIX + userId + ":tradeNo";
        //生成一个交易码
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        //放入缓存中
        redisTemplate.opsForValue().set(tradeNoKey,tradeNo);
        return tradeNo;
    }

    //提交订单
    @Transactional
    public Long saveOrderInfo(OrderInfo orderInfo) {
        orderInfo.sumTotalAmount();//计算总金额
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());//设置订单状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        //封装订单id
        String outTradeNo = "智能优选" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setCreateTime(new Date());//设置订单生成时间
        //设置订单过期时间为一天
        Calendar instance = Calendar.getInstance();//获取日历的实例
        instance.add(Calendar.DATE,1);//设置为一天
        orderInfo.setExpireTime(instance.getTime());
        //设置订单明细
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        StringBuffer tradeBody  = new StringBuffer();
        for (OrderDetail orderDetail : orderDetailList){
            tradeBody.append(orderDetail.getSkuName());//添加商品名称
        }
        if (tradeBody.toString().length() > 100){
            //设置订单描述
            orderInfo.setTradeBody(tradeBody.toString().substring(0,100));//截取100个字节
        }else{
            orderInfo.setTradeBody(tradeBody.toString());
        }
        //提交订单
        orderInfoMapper.insert(orderInfo);
        //设置订单详情的id
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        }
        //  TODO 删除购物车
        //发送延迟队列,如果定时未支付,取消订单
        rabbitService.sendDelayMessage(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL, MqConst.ROUTING_ORDER_CANCEL,
                orderInfo.getId(), MqConst.DELAY_TIME);//120秒过期
        return orderInfo.getId();
    }
}
