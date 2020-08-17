package com.suchengyu.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.product.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * SpuSaleAttrValueMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-17
 * @Description:
 */
@Mapper
@Repository
public interface SpuSaleAttrValueMapper extends BaseMapper<SpuSaleAttrValue> {
}
