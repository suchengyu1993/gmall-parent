package com.suchengyu.gmall.item.service;

import java.util.Map;

/**
 * ItemService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-20
 * @Description:
 */
public interface ItemService {
    //调用product商品基础服务查询数据
    Map<String, Object> getItem(Long skuId);

    Map<String, Object> getItemJUC(Long skuId);

}
