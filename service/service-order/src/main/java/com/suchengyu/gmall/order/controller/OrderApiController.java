package com.suchengyu.gmall.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.suchengyu.gmall.cart.client.CartFeignClient;
import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.common.util.AuthContextHolder;
import com.suchengyu.gmall.model.cart.CartInfo;
import com.suchengyu.gmall.model.order.OrderDetail;
import com.suchengyu.gmall.model.order.OrderInfo;
import com.suchengyu.gmall.model.user.UserAddress;
import com.suchengyu.gmall.model.ware.WareOrderTask;
import com.suchengyu.gmall.model.ware.WareOrderTaskDetail;
import com.suchengyu.gmall.order.service.OrderApiService;
import com.suchengyu.gmall.product.controller.ProductFeignClient;
import com.suchengyu.gmall.user.client.UserFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OrderApiController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-07
 * @Description:
 */
@Api(description = "订单接口")
@RequestMapping("/api/order")
@RestController
public class OrderApiController {
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private OrderApiService orderApiService;

    /**
     * 拆单业务
     * @param request
     * @return
     */
    @RequestMapping("/orderSplit")
    public List<WareOrderTask> orderSplit(HttpServletRequest request){
        String orderId = request.getParameter("orderId");
        // [{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
        String wareSkuMap = request.getParameter("wareSkuMap");
        ArrayList<WareOrderTask> wareOrderTasks = new ArrayList<>();
        OrderInfo orderInfo = orderApiService.getOrderInfo(Long.parseLong(orderId));
        //拆单规则,根据货物,商家,类型,物流,价值
        //解析json数据
        HashMap<String, Object> mapResult = new HashMap<>();
        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        List<Map> maps = JSON.parseArray(wareSkuMap, Map.class);
        for (Map<String, Object> map : maps) {
            String wareId = (String) map.get("wareId");
            JSONArray skuIds = (JSONArray) map.get("skuIds");
            WareOrderTask wareOrderTask = new WareOrderTask();
            ArrayList<WareOrderTaskDetail> wareOrderTaskDetails = new ArrayList<>();
            for (Object skuIdObject : skuIds) {
               String skuId = (String) skuIdObject;
                WareOrderTaskDetail wareOrderTaskDetail = new WareOrderTaskDetail();
                List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
                for (OrderDetail orderDetail : orderDetailList) {
                   String skuIdCheck =  orderDetail.getSkuId() + "";
                   if (skuIdCheck.equals(skuId)){
                       wareOrderTaskDetail.setSkuNum(orderDetail.getSkuNum());
                   }
                }
                wareOrderTaskDetail.setSkuId(skuId);
                wareOrderTaskDetails.add(wareOrderTaskDetail);
            }
            wareOrderTask.setDetails(wareOrderTaskDetails);
            wareOrderTask.setWareId(wareId);
            wareOrderTasks.add(wareOrderTask);
        }
        return wareOrderTasks;
    }

    @ApiOperation(value = "提交订单,跳转到支付页面")
    @GetMapping("/auth/getOrderInfo/{orderId}")
    public OrderInfo getOrderInfo(@PathVariable("orderId") long orderId){
        return orderApiService.getOrderInfo(orderId);
    }


    @ApiOperation(value = "生成交易码")
    @GetMapping("/auth/getTradeNo/{userId}")
    public String getTradeNo(@PathVariable("userId") String userId){
        String tradeNo = orderApiService.getTradeNo(userId);
        return tradeNo;
    }

    @ApiOperation(value = "提交订单")
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(HttpServletRequest request,@RequestBody OrderInfo orderInfo,String tradeNo){
        //获取用户Id
        String userId = AuthContextHolder.getUserId(request);
        orderInfo.setUserId(Long.parseLong(userId));
        //比较交易码
        boolean flag =  orderApiService.checkTradeCode(userId,tradeNo);
        if (!flag){
            return Result.fail().message("不能重复提交订单!");
        }
        //删除交易码
        orderApiService.deleteTradeNo(userId);
        //验证库存
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            //验证库存
            boolean result = orderApiService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!result){
                return Result.fail().message(orderDetail.getSkuName() + ", 库存不足!");
            }
            //验证价格
            BigDecimal skuPrice = productFeignClient.getSkuPrice(orderDetail.getSkuId());
            if (orderDetail.getOrderPrice().compareTo(skuPrice) != 0){
                //重新查询价格,更新缓存
                cartFeignClient.loadCartCache(userId);
                return Result.fail().message(orderDetail.getSkuName() + "您的收货地址不相符,请刷新确认收货地址");
            }
        }
        //验证通过,保存订单
        Long orderId = orderApiService.saveOrderInfo(orderInfo);
        return Result.ok(orderId);
    }

    @ApiOperation(value = "购物车结算功能,确认订单")
    @GetMapping("/auth/trade")
    public Result<Map<String, Object>> trade(HttpServletRequest request){
        //获取用户id
        String userId = AuthContextHolder.getUserId(request);
        //获取用户地址
        List<UserAddress> userAddressList = userFeignClient.getUserAddresses(userId);
        //渲染送货清单
        HashMap<String, Object> map = new HashMap<>();
        //得到用户想要购买的商品
        List<CartInfo> cartInfoList = cartFeignClient.getCartList(userId);
        ArrayList<OrderDetail> detailArrayList = new ArrayList<>();
        for (CartInfo info : cartInfoList) {
            if (info.getIsChecked() == 1){
                OrderDetail orderDetail = new OrderDetail();//订单详情
                orderDetail.setSkuId(info.getSkuId());//商品id
                orderDetail.setSkuName(info.getSkuName());//商品名称
                orderDetail.setImgUrl(info.getImgUrl());//商品图片
                orderDetail.setSkuNum(info.getSkuNum());//数量
                BigDecimal skuPrice = productFeignClient.getSkuPrice(info.getSkuId());//查询数据库获取价格
                orderDetail.setOrderPrice(skuPrice);//单价
//                orderDetail.setOrderPrice(skuPrice.multiply(new BigDecimal(info.getSkuNum())));//计算总价
                detailArrayList.add(orderDetail);
            }
        }
        // 计算总金额
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(detailArrayList);
        orderInfo.sumTotalAmount();

        map.put("userAddressList", userAddressList);
        map.put("detailArrayList", detailArrayList);
        map.put("totalNum", detailArrayList.size());//商品数量
        map.put("totalAmount", orderInfo.getTotalAmount());//总金额
        return Result.ok(map);
    }
}
