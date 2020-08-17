package com.suchengyu.gmall.all.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.item.client.ItemFeignClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * ItemController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-18
 * @Description:
 */
@Api(description = "前台商品详情")
@Controller
public class ItemController {
    @Autowired
    private ItemFeignClient itemFeignClient;

    @ApiOperation(value = "根据skuId获取sku商品详情")
    @RequestMapping("/{skuId}.html")
    public String getItem(
            @ApiParam(name = "skuId" ,value = "商品skuId",required =true )
            @PathVariable("skuId")Long skuId, Model model){
        //查询分类数据,item查询的是汇总数据,包括分类集合,商品详情,图片集合,销售属性集合等
        Result<Map<String,Object>> result =  itemFeignClient.getItem(skuId);
        model.addAllAttributes(result.getData());//result.getData()返回一个map集合
        return "item/index";//index.html
    }
}
