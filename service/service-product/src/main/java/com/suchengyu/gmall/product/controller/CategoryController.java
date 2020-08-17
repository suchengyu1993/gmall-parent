package com.suchengyu.gmall.product.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.product.BaseCategory1;
import com.suchengyu.gmall.model.product.BaseCategory2;
import com.suchengyu.gmall.model.product.BaseCategory3;
import com.suchengyu.gmall.product.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-13
 * @Description:
 */
@Api(description = "商品基础属性接口")
@CrossOrigin    //跨域
@RestController
@RequestMapping("/admin/product")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "查询所有一级分类信息")
    @GetMapping("/getCategory1")
    public Result getCategory1(){
       List<BaseCategory1> category1List =  categoryService.getCategory1();
        return Result.ok(category1List);
    }

    @ApiOperation(value = "根据一级分类id查询所有二级分类信息")
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(
            @ApiParam(name = "category1Id" ,value = "一级分类id",required =true )
            @PathVariable("category1Id")String category1Id
    ){
        List<BaseCategory2> category2s =  categoryService.getCategory2(category1Id);
        return Result.ok(category2s);
    }

    @ApiOperation(value = "根据二级分类id查询所有三级分类信息")
    @GetMapping("/getCategory3/{Category2Id}")
    public Result getCategory3(
            @ApiParam(name = "Category2Id" ,value = "二级分类id",required =true )
            @PathVariable("Category2Id")String Category2Id){
        List<BaseCategory3> category3s =  categoryService.getCategory3(Category2Id);
        return Result.ok(category3s);
    }
}
