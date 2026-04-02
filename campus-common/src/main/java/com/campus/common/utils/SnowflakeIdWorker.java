package com.campus.common.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * 雪花ID生成器
 * <p>
 * 基于MyBatis-Plus内置的IdWorker实现，使用ASSIGN_ID策略生成分布式唯一ID。
 * </p>
 */
public class SnowflakeIdWorker {

    private SnowflakeIdWorker() {
    }

    /**
     * 生成下一个唯一ID
     *
     * @return 唯一ID（Long）
     */
    public static long nextId() {
        return IdWorker.getId();
    }

    /**
     * 生成下一个唯一ID（字符串形式）
     *
     * @return 唯一ID（String）
     */
    public static String nextIdStr() {
        return IdWorker.getIdStr();
    }
}
