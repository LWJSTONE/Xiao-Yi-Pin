package com.campus.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装
 *
 * @param <T> 记录类型
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据列表 */
    private List<T> records;

    /** 总记录数 */
    private long total;

    /** 每页大小 */
    private long size;

    /** 当前页码 */
    private long current;

    public PageResult() {
    }

    public PageResult(List<T> records, long total, long size, long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
    }

    /**
     * 从MyBatis-Plus的IPage构建
     */
    public static <T> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setCurrent(page.getCurrent());
        return result;
    }
}
