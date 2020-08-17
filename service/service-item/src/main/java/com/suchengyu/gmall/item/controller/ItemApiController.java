package com.suchengyu.gmall.item.controller;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ItemFeignClient
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-18
 * @Description:
 */
@RestController
@RequestMapping("/api/item")
public class ItemApiController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("/{skuId}")
    public Result<Map<String,Object>> getItem(@PathVariable("skuId")Long SkuId){
        //调用product商品基础服务查询数据
        Map<String,Object> map1 = itemService.getItemJUC(SkuId);
        Map<String,Object> map = itemService.getItem(SkuId);

        return Result.ok(map);
    }

}
