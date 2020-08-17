package com.suchengyu.gmall.product.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.product.BaseAttrInfo;
import com.suchengyu.gmall.model.product.BaseAttrValue;
import com.suchengyu.gmall.product.service.AttrInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AttrInfoController
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
@Api(description = "平台属性接口")
@CrossOrigin    //跨域
@RestController
@RequestMapping("/admin/product")
public class AttrInfoController {
    @Autowired
    private AttrInfoService attrInfoService;

    @ApiOperation(value = "根据分类id获取平台属性")
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(
            @ApiParam(name = "category1Id" ,value = "一级分类id",required =true )
            @PathVariable("category1Id")String category1Id,
            @ApiParam(name = "category2Id" ,value = "二级分类id",required =true )
            @PathVariable("category2Id")String category2Id,
            @ApiParam(name = "category3Id" ,value = "三级分类id",required =true )
            @PathVariable("category3Id")String category3Id){
        List<BaseAttrInfo> list = attrInfoService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(list);
    }

    @ApiOperation(value = "添加或修改平台属性与平台属性值")
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(
            @ApiParam(name = "baseAttrInfo" ,value = "平台属性",required =true )
            @RequestBody BaseAttrInfo baseAttrInfo){
        attrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @ApiOperation(value = "根据平台属性ID获取平台属性值")
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(
            @ApiParam(name = "attrId" ,value = "平台属性id",required =true )
            @PathVariable("attrId")Long attrId){
        List<BaseAttrValue> baseAttrValueList = attrInfoService.getAttrValueList(attrId);
        return Result.ok(baseAttrValueList);

    }
}
