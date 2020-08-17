package com.suchengyu.gmall.list.service;

import com.suchengyu.gmall.model.list.SearchParam;
import com.suchengyu.gmall.model.list.SearchResponseVo;

/**
 * ListApiService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-28
 * @Description:
 */
public interface ListApiService {

    //商品上架
    void upperGoods(Long skuId);

    //商品下架
    void lowerGoods(Long skuId);

    //更新商品时,增加热度
    void incrHotScore(Long skuId);

    //列表搜索商品
    SearchResponseVo list(SearchParam searchParam);

}
