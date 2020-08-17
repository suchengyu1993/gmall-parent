package com.suchengyu.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.product.SkuInfo;
import com.suchengyu.gmall.product.service.SkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * SkuController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-17
 * @Description:
 */
@Api(description = "Sku功能接口")
@CrossOrigin    //跨域
@RestController
@RequestMapping("/admin/product")
public class SkuController {
    @Autowired
    private SkuService skuService;

    @ApiOperation(value = "Sku商品下架")
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(
            @ApiParam(name = "skuId" ,value = "当前商品的skuId",required =true )
            @PathVariable("skuId")Long skuId){
        skuService.cancelSale(skuId);
        return Result.ok();
    }


    @ApiOperation(value = "Sku商品上架")
    @GetMapping("/onSale/{skuId}")
    public Result onSale(
            @ApiParam(name = "skuId" ,value = "当前商品的skuId",required =true )
            @PathVariable("skuId")Long skuId){
        skuService.onSale(skuId);
        return Result.ok();
    }

    @ApiOperation(value = "分页显示Sku列表")
    @GetMapping("/list/{page}/{limit}")
    public Result list(
            @ApiParam(name = "page" ,value = "当前页",required =true )
            @PathVariable("page")Long page,
            @ApiParam(name = "limit" ,value = "每页显示条数",required =true )
            @PathVariable("limit")Long limit){
        Page skuInfoPage = new Page(page, limit);
        IPage<SkuInfo> infoIPage = skuService.list(skuInfoPage);
        return Result.ok(infoIPage);
    }

    @ApiOperation(value = "Sku保存功能")
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(
            @ApiParam(name = "skuInfo" ,value = "Sku的基本信息对象",required =true )
            @RequestBody SkuInfo skuInfo){
        skuService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

}
