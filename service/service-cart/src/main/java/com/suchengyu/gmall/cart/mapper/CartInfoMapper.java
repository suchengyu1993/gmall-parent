package com.suchengyu.gmall.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.cart.CartInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * CartInfoMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-06
 * @Description:
 */
@Mapper
@Repository
public interface CartInfoMapper extends BaseMapper<CartInfo> {
}
