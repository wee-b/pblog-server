package com.pblog.common.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class RandomCodeUtil {
    // 仅包含数字字符
    private static final String NUMBERS = "0123456789";
    // 小写字母
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    // 大写字母
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // 特殊字符（可根据需求调整，避免易混淆字符如!和l、0和O等）
    private static final String SPECIAL_CHARS = "!@#$%^&*()_-+=<>?";
    // 默认验证码长度（6位数字）
    private static final int DEFAULT_NUMERIC_LENGTH = 6;
    // 强随机数生成器（线程安全，比Random更安全）
    private static final SecureRandom RANDOM = new SecureRandom();

    static {
        // 初始化时加入随机种子（增强随机性）
        RANDOM.setSeed(SecureRandom.getSeed(16));
    }

    /**
     * 生成默认长度（6位）的纯数字验证码
     */
    public static String generate() {
        return generateNumericCode(DEFAULT_NUMERIC_LENGTH);
    }

    /**
     * 生成指定长度的纯数字验证码
     *
     * @param length 验证码长度（需大于0）
     */
    public static String generateNumericCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(NUMBERS.length());
            sb.append(NUMBERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成包含数字和字母（大小写）的混合验证码
     *
     * @param length 验证码长度（需大于0）
     */
    public static String generateAlphanumericCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }
        // 数字+大小写字母的混合字符集
        String chars = NUMBERS + LOWER_CASE + UPPER_CASE;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成包含数字、字母和特殊字符的强密码级随机字符串
     *
     * @param length 字符串长度（建议至少8位）
     */
    public static String generateStrongRandomString(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("强随机字符串长度建议至少8位");
        }
        // 包含所有字符类型的混合字符集
        String chars = NUMBERS + LOWER_CASE + UPPER_CASE + SPECIAL_CHARS;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成10位唯一数字字符串（可用于用户名、会员号等场景）
     * 范围：1000000000 ~ 9999999999
     */
    public static String generate10DigitNumber() {
        // 生成10位数字（确保首位不为0）
        long min = 1000000000L;
        long max = 9999999999L;
        // 计算范围内的随机数：min + (long)(random * (max - min + 1))
        long number = min + (long) (RANDOM.nextDouble() * (max - min + 1));
        return String.valueOf(number);
    }

    /**
     * 生成简化的UUID（去除横线，32位）
     */
    public static String generateSimpleUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}