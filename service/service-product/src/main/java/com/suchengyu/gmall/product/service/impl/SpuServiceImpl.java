package com.suchengyu.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suchengyu.gmall.common.cache.GmallCache;
import com.suchengyu.gmall.model.product.*;
import com.suchengyu.gmall.product.mapper.*;
import com.suchengyu.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SpuServiceImpl
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;

    //查询销售属性集合
    @GmallCache(prefix = "sku:",suffix = ":saleAttr")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku( Long skuId,Long spuId) {
        //使用自己写的sql
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.SelectSpuSaleAttrListCheckBySku(spuId,skuId);
/*        QueryWrapper<SpuSaleAttr> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectList(wrapper);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            Long baseSaleAttrId = spuSaleAttr.getBaseSaleAttrId();
            QueryWrapper<SpuSaleAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("spu_id", spuId);
            queryWrapper.eq("base_sale_attr_id", baseSaleAttrId);
            List<SpuSaleAttrValue> saleAttrValueList = spuSaleAttrValueMapper.selectList(queryWrapper);
            spuSaleAttr.setSpuSaleAttrValueList(saleAttrValueList);
        }*/
        return spuSaleAttrList;
    }

    //查询spu的图片集合
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(wrapper);
        return spuImageList;
    }

    //查询spu销售属性列表
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        // 1. 根据spuId查出销售属性集合
        QueryWrapper<SpuSaleAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectList(queryWrapper);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            // 2.根据spuId和平台属性id差销售属性值
            QueryWrapper<SpuSaleAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("spu_id", spuId);
            wrapper.eq("base_sale_attr_id", spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(wrapper);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }
        return spuSaleAttrList;
    }

    //添加spu基本信息
    public void saveSpuInfo(SpuInfo spuInfo) {
        // 1. 保存spu基本信息
        spuInfoMapper.insert(spuInfo);
        Long spuId = spuInfo.getId();
        // 2. 保存spu销售属性信息
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuId);
            spuSaleAttrMapper.insert(spuSaleAttr);
            // 3. 保存spu销售属性值信息
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(spuId);
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }
        }
        // 4. 保存spu图片集合信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuId);
            spuImageMapper.insert(spuImage);
        }
    }

    //分页查询spu数据
    public IPage<SpuInfo> pageList(Page pageParam, SpuInfo spuInfo) {
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id",spuInfo.getCategory3Id());
        IPage<SpuInfo> iPage = spuInfoMapper.selectPage(pageParam, wrapper);
        return iPage;
    }

    //获取销售属性
    public List<BaseSaleAttr> baseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrList;
    }

    //获取平台属性
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

}
