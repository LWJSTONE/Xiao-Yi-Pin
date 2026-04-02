package com.campus.common.constant;

/**
 * Redis Key常量
 */
public class RedisConstant {

    private RedisConstant() {
    }

    /** 登录Token Key前缀 */
    public static final String LOGIN_TOKEN_KEY = "login:token:";

    /** 验证码Key前缀 */
    public static final String CAPTCHA_KEY = "captcha:";

    /** 用户黑名单Key前缀 */
    public static final String USER_BLACKLIST = "user:blacklist:";

    /** 岗位分布式锁Key前缀 */
    public static final String JOB_LOCK = "lock:job:";
}
