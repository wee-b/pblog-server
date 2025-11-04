package com.pblog.common.constant;

import lombok.Getter;

/**
 * 枚举类，常量
 */
@Getter
public enum EmailConstants {
    EMAIL_SEND_SUCCESS("皇上，臣妾已为您发送了邮箱哦！"),//邮件发送成功，请查收！
    EMAIL_MESSAGE("您的QQ邮箱验证码为："),
    EMAIL_OUTTIME_TEN("，请在2分钟内完成验证"),
    EMAIL_TITLE("QQ邮箱验证码"),// 邮箱验证码发送的标题
    EMAIL_CODE("email_code"),// 邮箱验证码redis的key
    EMAIL_CODE_ERROR("皇上，请核实您的QQ邮箱验证码是否正确！"),// qq邮箱验证码错误
    NOT_EXIST_EMAIL("皇上，臣妾没有找到您的QQ邮箱，请确认是否正确！");// 邮箱不存在

    private final String Value;

    EmailConstants(String value){
        this.Value = value;
    }
}