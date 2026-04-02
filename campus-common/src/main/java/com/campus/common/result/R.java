package com.campus.common.result;

import com.campus.common.exception.BusinessException;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装
 *
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 时间戳 */
    private long timestamp;

    private R() {
        this.timestamp = System.currentTimeMillis();
    }

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> R<T> ok() {
        return new R<>(200, "success", null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<>(200, message, data);
    }

    /**
     * 失败响应（带消息）
     */
    public static <T> R<T> error(String message) {
        return new R<>(500, message, null);
    }

    /**
     * 失败响应（带状态码和消息）
     */
    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }

    /**
     * 失败响应（从BusinessException构建）
     */
    public static <T> R<T> error(BusinessException e) {
        return new R<>(e.getCode(), e.getMessage(), null);
    }
}
