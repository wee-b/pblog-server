package com.pblog.common.vo;

import cn.hutool.captcha.LineCaptcha;
import lombok.Data;

/**
 * 验证码返回DTO：封装 UUID 和验证码对象
 */
@Data
public class CaptchaVO {
    private String captchaUuid; // 验证码唯一标识
    private LineCaptcha captcha; // Hutool 生成的验证码（包含图片流和验证码内容）
}
