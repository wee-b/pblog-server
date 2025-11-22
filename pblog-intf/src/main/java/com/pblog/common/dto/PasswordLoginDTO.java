package com.pblog.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordLoginDTO {
    private String username;
    private String password;
    // 图形验证码相关
    private String captchaUuid;
    private String captchaCode;
}
