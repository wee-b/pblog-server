package com.pblog.common.constant;

public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final String LOGIN_TOKEN_KEY = "login:token:";
    public static final String LOGIN_EmailCode_KEY = "login:email:code:";

    // 单位是秒
    public static final Integer CODE_EXPIRE = 120;


    /**
     * 构建用户点赞集合Key
     */
    public static String buildUserLikeKey(String targetType, Integer targetId) {
        return String.format("like:user:%s:%s", targetType, targetId);
    }

    /**
     * 构建点赞总数Key
     */
    public static String buildCountKey(String targetType, Integer targetId) {
        return String.format("like:count:%s:%s", targetType, targetId);
    }

    public static final Integer Like_Set_TTL = 60;  // 单位分钟

}
