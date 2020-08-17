package com.suchengyu.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suchengyu.gmall.model.product.*;

import java.util.List;

/**
 * SpuService
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */

public interface SpuService {
    //分页查询spu数据
    IPage<SpuInfo> pageList(Page pageParam, SpuInfo spuInfo);

    //获取销售属性
    List<BaseSaleAttr> baseSaleAttrList();

    //获取平台属性
    List<BaseTrademark> getTrademarkList();

    //添加spu基本信息
    void saveSpuInfo(SpuInfo spuInfo);

    //查询spu销售属性列表
    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    //查询spu的图片集合
    List<SpuImage> spuImageList(Long spuId);

    //查询销售属性集合
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId,Long spuId);

}
