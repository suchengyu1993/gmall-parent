package com.suchengyu.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.product.SpuInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * SpuInfoMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
@Mapper
@Repository
public interface SpuInfoMapper extends BaseMapper<SpuInfo> {
}
