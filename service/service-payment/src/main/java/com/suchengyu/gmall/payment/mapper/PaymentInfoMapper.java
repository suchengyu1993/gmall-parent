package com.suchengyu.gmall.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.payment.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * PaymentInfoMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-11
 * @Description:
 */
@Mapper
@Repository
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {
}
