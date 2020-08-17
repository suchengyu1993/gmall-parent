package com.suchengyu.gmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.order.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * OrderDetailMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-10
 * @Description:
 */
@Mapper
@Repository
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
