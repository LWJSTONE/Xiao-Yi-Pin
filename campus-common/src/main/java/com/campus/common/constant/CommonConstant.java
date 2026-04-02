package com.campus.common.constant;

/**
 * 公共常量
 */
public class CommonConstant {

    private CommonConstant() {
    }

    /** Token请求头 */
    public static final String TOKEN_HEADER = "Authorization";

    /** Token前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 默认分页大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** 最大分页大小 */
    public static final int MAX_PAGE_SIZE = 100;
}
