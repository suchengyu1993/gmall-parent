package com.suchengyu.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.product.BaseSaleAttr;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * BaseSaleAttrMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-15
 * @Description:
 */
@Mapper
@Repository
public interface BaseSaleAttrMapper extends BaseMapper<BaseSaleAttr> {
}
