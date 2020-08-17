package com.suchengyu.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.list.SearchAttr;
import com.suchengyu.gmall.model.product.BaseAttrInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BaseAttrInfoMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-07-14
 * @Description:
 */
@Mapper
@Repository
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    List<SearchAttr> selectBaseAttrInfoListBySkuId(@Param("skuId") Long skuId);
}
