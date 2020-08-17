package com.suchengyu.gmall.list.client;

import com.suchengyu.gmall.common.result.Result;
import com.suchengyu.gmall.model.list.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ListFeignClient
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-28
 * @Description:
 */
@FeignClient("service-list")
public interface ListFeignClient {
    /**
     * 商品上架
     */
    @RequestMapping("/api/list/inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId);

    /**
     *商品下架
     */
    @RequestMapping("/api/list/inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId);

    //更新商品时,增加热度
    @RequestMapping("/api/list/inner/incrHotScore/{skuId}")
    void incrHotScore(@PathVariable("skuId")Long skuId);

    //搜索商品
    @PostMapping("/api/list")
    Result list(@RequestBody SearchParam searchParam);

}
