package com.suchengyu.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.product.*;
import com.suchengyu.gmall.product.service.SpuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SpuController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
@Api(description = "Spu功能接口")
@CrossOrigin    //跨域
@RestController
@RequestMapping("/admin/product")
public class SpuController {
    @Autowired
    private SpuService spuService;

    @ApiOperation(value = "查询spu的图片集合")
    @GetMapping("/spuImageList/{spuId}")
    public Result spuImageList(
            @ApiParam(name = "spuId" ,value = "spu的id",required =true )
            @PathVariable("spuId")Long spuId){
       List<SpuImage> spuImageList = spuService.spuImageList(spuId);
       return Result.ok(spuImageList);
    }


    @ApiOperation(value ="查询spu销售属性列表")
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(
            @ApiParam(name = "spuId" ,value = "spu的id",required =true )
            @PathVariable("spuId")Long spuId){
      List<SpuSaleAttr> spuSaleAttrList =  spuService.spuSaleAttrList(spuId);
        return  Result.ok(spuSaleAttrList);
    }

    @ApiOperation(value = "添加spu基本信息")
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(
            @ApiParam(name = "spuInfo" ,value = "添加对象",required =true )
            @RequestBody SpuInfo spuInfo){
        spuService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    @ApiOperation(value = "分页查询spu数据")
    @GetMapping("/{page}/{limit}")
    public Result pageList(
            @ApiParam(name = "page" ,value = "当前页",required =true )
            @PathVariable("page")Long page,
            @ApiParam(name = "limit" ,value = "每页数量",required =true )
            @PathVariable("limit")Long limit,
            @ApiParam(name = "spuInfo" ,value = "查询对象",required =true )
            SpuInfo spuInfo){
        Page pageParam = new Page(page, limit);
        IPage<SpuInfo> iPage = spuService.pageList(pageParam,spuInfo);
        return Result.ok(iPage);
    }
    @ApiOperation(value = "获取销售属性")
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
       List<BaseSaleAttr> baseSaleAttrList =  spuService.baseSaleAttrList();
       return Result.ok(baseSaleAttrList);
    }

    @ApiOperation(value = "获取平台属性")
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> baseTrademarkList =  spuService.getTrademarkList();
        return Result.ok(baseTrademarkList);
    }
}
