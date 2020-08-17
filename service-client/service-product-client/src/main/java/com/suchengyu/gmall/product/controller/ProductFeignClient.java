package com.suchengyu.gmall.product.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.list.SearchAttr;
import com.suchengyu.gmall.model.product.BaseCategoryView;
import com.suchengyu.gmall.model.product.BaseTrademark;
import com.suchengyu.gmall.model.product.SkuInfo;
import com.suchengyu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ProductFeignClient
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-18
 * @Description:
 */
@FeignClient(value = "service-product")
public interface ProductFeignClient {

    //根据skuId查出商品基本信息
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId")Long skuId);

    //根据三级分类id查出分类信息
    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id);

    //根据skuId查询价格
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId);

    //查询销售属性集合
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId);

    //商品销售属性对应的skuId的map
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    List<Map<String, Object>> getSkuValueIdsMap(@PathVariable("spuId") Long spuId);

    //查询sku对应的平台属性信息
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    List<SearchAttr> getAttrList(@PathVariable("skuId") Long skuId);

    //查询品牌信息
    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    BaseTrademark getTrademark(@PathVariable("tmId") Long tmId);

    //访问首页
    @GetMapping("/api/product/inner/getBaseCategoryList")
    Result getBaseCategoryList();


}
