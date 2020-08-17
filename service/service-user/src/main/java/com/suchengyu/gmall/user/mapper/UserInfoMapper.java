package com.suchengyu.gmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suchengyu.gmall.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * UserInfoMapper
 *
 * @Author: 苏成瑜
 * @CreateTime: 2020-08-04
 * @Description:
 */
@Mapper
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
