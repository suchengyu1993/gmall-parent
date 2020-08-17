package com.suchengyu.gmall.list.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.list.service.ListApiService;
import com.suchengyu.gmall.model.list.Goods;
import com.suchengyu.gmall.model.list.SearchParam;
import com.suchengyu.gmall.model.list.SearchResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * ListApiController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-28
 * @Description:
 */
@Api(description = "后台list商品接口")
@RequestMapping("/api/list")
@RestController
public class ListApiController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private ListApiService listApiService;

    //搜索商品
    @ApiOperation(value = "列表搜索商品")
    @PostMapping()
    public Result list(
            @ApiParam(name = "searchParam" ,value = "搜索的关键数据",required =true )
            @RequestBody SearchParam searchParam){
        SearchResponseVo responseVo =  listApiService.list(searchParam);
      return Result.ok(responseVo);
    }

    //更新商品时,增加热度
    @ApiOperation(value = "更新商品时,增加热度")
    @RequestMapping("/inner/incrHotScore/{skuId}")
    public void incrHotScore(
            @ApiParam(name = "skuId" ,value = "商品skuId",required =true )
            @PathVariable("skuId")Long skuId){
        listApiService.incrHotScore(skuId);
    }

    /**
     * 创建索引与数据结构
     */
    @RequestMapping("/inner/createIndex")
    public Result createIndex(){
        //创建索引
        elasticsearchRestTemplate.createIndex(Goods.class);
        //创建数据库结构
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 商品上架
     */
    @ApiOperation(value = "商品上架")
    @RequestMapping("/inner/upperGoods/{skuId}")
    public Result upperGoods(
            @ApiParam(name = "skuId" ,value = "商品skuId",required =true )
            @PathVariable("skuId") Long skuId){
        listApiService.upperGoods(skuId);
        return Result.ok();
    }

    /**
     *商品下架
     */
    @ApiOperation(value = "商品下架")
    @RequestMapping("/inner/lowerGoods/{skuId}")
    public Result lowerGoods(
            @ApiParam(name = "skuId" ,value = "商品skuId",required =true )
            @PathVariable("skuId") Long skuId){
        listApiService.lowerGoods(skuId);
        return Result.ok();
    }

}
