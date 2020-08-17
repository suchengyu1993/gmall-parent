package com.suchengyu.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SpuSaleAttrMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-17
 * @Description:
 */
@Mapper
@Repository
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    List<SpuSaleAttr> SelectSpuSaleAttrListCheckBySku(Long spuId, Long skuId);
}
