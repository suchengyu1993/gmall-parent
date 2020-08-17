package com.suchengyu.gmall.item.client;

import com.suchengyu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * ItemFeignClient
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-18
 * @Description:
 */
@FeignClient(value = "service-item")
public interface ItemFeignClient {

    @RequestMapping("/api/item/{skuId}")
    public Result<Map<String,Object>> getItem(@PathVariable("skuId")Long skuId);

}
