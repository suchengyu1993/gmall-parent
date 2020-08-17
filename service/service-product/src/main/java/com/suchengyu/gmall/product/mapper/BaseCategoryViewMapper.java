package com.suchengyu.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.product.BaseCategoryView;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * BaseCategoryViewMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-20
 * @Description:
 */
@Mapper
@Repository
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {
}
