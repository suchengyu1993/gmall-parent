package com.suchengyu.gmall.product.service;

import com.suchengyu.gmall.model.product.BaseTrademark;

/**
 * BaseTrademarkService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-28
 * @Description:
 */

public interface BaseTrademarkService {
    //查询品牌信息
    BaseTrademark getTrademark(Long tmId);
}
