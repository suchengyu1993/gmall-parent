package com.suchengyu.gmall.product.service.impl;

import com.suchengyu.gmall.model.product.BaseTrademark;
import com.suchengyu.gmall.product.mapper.BaseTrademarkMapper;
import com.suchengyu.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BasetrademarkServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-28
 * @Description:
 */
@Service
public class BasetrademarkServiceImpl implements BaseTrademarkService {
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    //查询品牌信息
    public BaseTrademark getTrademark(Long tmId) {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(tmId);
        return baseTrademark;
    }
}
