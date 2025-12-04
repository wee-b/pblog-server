package com.pblog.common.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailCodeDTO {
    private String email;
    private String code;
    // 图形验证码相关
    private String captchaUuid;
    private String captchaCode;
}

