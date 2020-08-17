package com.suchengyu.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suchengyu.gmall.model.product.SkuInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * SkuService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-17
 * @Description:
 */

public interface SkuService {

    //Sku保存功能
    void saveSkuInfo(SkuInfo skuInfo);

    //分页显示Sku列表
    IPage<SkuInfo> list(Page skuInfoPage);

    //Sku商品上架
    void onSale(Long skuId);

    //Sku商品下架
    void cancelSale(Long skuId);

    //根据skuId查询商品基本信息
    SkuInfo getSkuInfo(Long skuId);

    //根据skuId查询价格
    BigDecimal getSkuPrice(Long skuId);

    //商品销售属性对应的skuId的map
    List<Map<String, Object>> getSkuValueIdsMap(Long spuId);

    //使用缓存获取sku基本信息
    SkuInfo getSkuInfoNx(Long skuId);

    //整合aop的缓存
    SkuInfo getSkuInfoAop(Long skuId);
}
