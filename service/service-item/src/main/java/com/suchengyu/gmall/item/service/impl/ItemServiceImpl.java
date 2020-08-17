package com.suchengyu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.suchengyu.gmall.item.service.ItemService;
import com.suchengyu.gmall.list.client.ListFeignClient;
import com.suchengyu.gmall.model.product.BaseCategoryView;
import com.suchengyu.gmall.model.product.SkuInfo;
import com.suchengyu.gmall.model.product.SpuSaleAttr;
import com.suchengyu.gmall.product.controller.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * ItemServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-20
 * @Description:
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ListFeignClient listFeignClient;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    //调用product商品基础服务查询数据
    public Map<String, Object> getItemJUC(Long skuId) {
        //测试时间差
        long currentTimeMillisStrart = System.currentTimeMillis();
        System.out.println("多线程开始时间:" + currentTimeMillisStrart);
        HashMap<String, Object> map = new HashMap<>();
        //查询sku的基本信息,需要返回值
        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                map.put("skuInfo", skuInfo);
                return skuInfo;
            }
        },threadPoolExecutor);
        //根据三级分类id查出分类信息
        CompletableFuture completableFutureCategory =  completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView =  productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                map.put("categoryView", baseCategoryView);
            }
        },threadPoolExecutor);
        //查询销售属性列表
        CompletableFuture completableFutureSaleAttrList = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                Long spuId = skuInfo.getSpuId();
                List<SpuSaleAttr> spuSaleAttrList =  productFeignClient.getSpuSaleAttrListCheckBySku(spuId,skuId);
                map.put("spuSaleAttrList", spuSaleAttrList);
            }
        },threadPoolExecutor);
        //商品销售属性对应的skuId的map
        CompletableFuture completableFutureSaleMap = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<Map<String,Object>> valueIds =  productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
                HashMap<String, String> valueSkuIdMap = new HashMap<>();
                for (Map<String, Object> stringObjectMap : valueIds) {
                    String k_value_ids = stringObjectMap.get("value_ids")+"";
                    String v_sku_id = stringObjectMap.get("sku_id")+"";
                    valueSkuIdMap.put(k_value_ids,v_sku_id);
                }
                map.put("valuesSkuJson", JSON.toJSONString(valueSkuIdMap));
            }
        },threadPoolExecutor);
        //根据skuId查询价格
        CompletableFuture ompletableFuturePrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price =productFeignClient.getSkuPrice(skuId);
                map.put("price",price);
            }
        },threadPoolExecutor);
        //es搜索的热度值
        CompletableFuture<Void> incrHotScoreFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                listFeignClient.incrHotScore(skuId);
            }
        },threadPoolExecutor);

        //主线程等待多线程结束
        CompletableFuture.allOf(completableFutureSkuInfo,completableFutureCategory,
                completableFutureSaleAttrList,completableFutureSaleMap,ompletableFuturePrice).join();
        //测试时间差
        long currentTimeMillisEnd = System.currentTimeMillis();
        System.out.println("多线程执行结束时间:" +(currentTimeMillisEnd - currentTimeMillisStrart));
        return map;
    }

    //调用product商品基础服务查询数据
    public Map<String, Object> getItem(Long skuId) {
        //测试时间差
        long currentTimeMillisStrart = System.currentTimeMillis();
        System.out.println("普通方法开始时间:" + currentTimeMillisStrart);
        HashMap<String, Object> map = new HashMap<>();
        //查询sku的基本信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //根据三级分类id查出分类信息
        BaseCategoryView baseCategoryView =  productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        //根据skuId查询价格
        BigDecimal price =productFeignClient.getSkuPrice(skuId);
        //查询销售属性列表
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrList =  productFeignClient.getSpuSaleAttrListCheckBySku(spuId,skuId);
        //商品销售属性对应的skuId的map
        List<Map<String,Object>> valueIds =  productFeignClient.getSkuValueIdsMap(spuId);
        HashMap<String, String> valueSkuIdMap = new HashMap<>();
        for (Map<String, Object> stringObjectMap : valueIds) {
            String k_value_ids = stringObjectMap.get("value_ids")+"";
            String v_sku_id = stringObjectMap.get("sku_id")+"";
            valueSkuIdMap.put(k_value_ids,v_sku_id);
        }
        map.put("skuInfo", skuInfo);
        map.put("categoryView", baseCategoryView);
        map.put("price",price);
        map.put("spuSaleAttrList", spuSaleAttrList);
        map.put("valuesSkuJson", JSON.toJSONString(valueSkuIdMap));
        //测试时间差
        long currentTimeMillisEnd = System.currentTimeMillis();
        System.out.println("普通方法结束时间:" +(currentTimeMillisEnd - currentTimeMillisStrart));
        return map;
    }
}
