package com.pblog.admin.controller;

import cn.hutool.captcha.LineCaptcha;
import com.pblog.common.vo.CaptchaVO;
import com.pblog.admin.service.CodeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RequestMapping("/code")
@RestController
@Slf4j
public class CodeController {

    @Autowired
    private CodeService codeService;


    @GetMapping("/picture/generate")
    public void generateCaptcha(HttpServletResponse response) throws IOException {
        // 1. 生成验证码
        CaptchaVO captchaVO = codeService.generateCaptcha();
        LineCaptcha captcha = captchaVO.getCaptcha();
        String captchaUuid = captchaVO.getCaptchaUuid();

        // 2. 设置响应头：返回 UUID（前端存储该 UUID，校验时传入）
        response.setHeader("Captcha-Uuid", captchaUuid);

        // 3. 设置响应格式：图片/png
        response.setContentType("image/png");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 4. 输出图片流到响应
        captcha.write(response.getOutputStream());
    }


}
