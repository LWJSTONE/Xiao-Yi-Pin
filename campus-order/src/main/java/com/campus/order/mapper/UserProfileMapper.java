package com.campus.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.common.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户详情Mapper（订单模块使用）
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
