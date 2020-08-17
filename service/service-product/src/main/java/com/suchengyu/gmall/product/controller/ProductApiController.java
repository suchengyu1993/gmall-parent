package com.suchengyu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.list.SearchAttr;
import com.suchengyu.gmall.model.product.BaseCategoryView;
import com.suchengyu.gmall.model.product.BaseTrademark;
import com.suchengyu.gmall.model.product.SkuInfo;
import com.suchengyu.gmall.model.product.SpuSaleAttr;
import com.suchengyu.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ProductApplication
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-18
 * @Description:
 */
@RestController
@RequestMapping("/api/product")
public class ProductApiController {
    @Autowired
    private SkuService skuService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuService spuService;
    @Autowired
    private AttrInfoService attrInfoService;
    @Autowired
    private BaseTrademarkService basetrademarkService;


    //访问首页 TODO
   @GetMapping("/inner/getBaseCategoryList")
   public Result getBaseCategoryList(HttpServletRequest request){
        String userId = request.getHeader("userId");
        List<JSONObject> baseCategoryList = categoryService.getBaseCategoryList();
        return Result.ok(baseCategoryList);
    }

    //查询sku对应的平台属性信息
    @GetMapping("/inner/getAttrList/{skuId}")
    public List<SearchAttr> getAttrList(@PathVariable("skuId") Long skuId){
        List<SearchAttr> searchAttrList =  attrInfoService.getAttrList(skuId);
        return searchAttrList;
    }

    //查询品牌信息
    @GetMapping("/inner/getTrademark/{tmId}")
   public BaseTrademark getTrademark(@PathVariable("tmId") Long tmId){
       BaseTrademark baseTrademark =  basetrademarkService.getTrademark(tmId);
       return baseTrademark;
    }

    //商品销售属性对应的skuId的map
    @GetMapping("/inner/getSkuValueIdsMap/{spuId}")
    public List<Map<String, Object>> getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
       List<Map<String,Object>> map = skuService.getSkuValueIdsMap(spuId);
        return map;
    }

    //查询销售属性集合
    @GetMapping("/inner/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
   public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId){
        List<SpuSaleAttr> spuSaleAttrList = spuService.getSpuSaleAttrListCheckBySku(skuId,spuId);
        return spuSaleAttrList;
    }

    //根据skuId查询价格
    @GetMapping("/inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId){
        BigDecimal  price = skuService.getSkuPrice(skuId);
        return price;
    }

    //根据skuId查询商品基本信息
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId")Long skuId){
        SkuInfo skuInfo =  skuService.getSkuInfoAop(skuId);
        return skuInfo;
    }

    //根据三级分类id查出分类信息
    @GetMapping("/inner/getCategoryView/{category3Id}")
   public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView baseCategoryView =  categoryService.getCategoryView(category3Id);
        return baseCategoryView;
    }
}
