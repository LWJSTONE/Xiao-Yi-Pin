package com.campus.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.common.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户详情Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
