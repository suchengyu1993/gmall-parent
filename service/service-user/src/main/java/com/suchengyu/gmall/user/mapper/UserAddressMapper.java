package com.suchengyu.gmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.user.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * UserAddressMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-07
 * @Description:
 */
@Mapper
@Repository
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
