package com.pblog.common.constant;

public class CaptchaConstants {
    /** 验证码过期时间：60秒 */
    public static final long CAPTCHA_EXPIRE_SECONDS = 60;
    /** Redis 验证码存储前缀（格式：captcha:uuid） */
    public static final String CAPTCHA_REDIS_KEY_PREFIX = "captcha:";
    /** 验证码图片宽度 */
    public static final int CAPTCHA_WIDTH = 120;
    /** 验证码图片高度 */
    public static final int CAPTCHA_HEIGHT = 40;
    /** 验证码字符长度 */
    public static final int CAPTCHA_LENGTH = 4;
}