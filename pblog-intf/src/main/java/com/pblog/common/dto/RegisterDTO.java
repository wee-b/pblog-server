package com.pblog.common.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String email;
    private String code;
    private String name;
    private String password;
    // 图形验证码相关
    private String captchaUuid;
    private String captchaCode;
}
