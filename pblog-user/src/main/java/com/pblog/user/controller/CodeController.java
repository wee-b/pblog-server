package com.pblog.user.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.result.ResponseResult;

import com.pblog.common.vo.CaptchaVO;
import com.pblog.user.service.CodeService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 第三方短信验证以及其他相关操作
 */
@RequestMapping("/code")
@RestController
@Slf4j
public class CodeController {

    @Autowired
    private CodeService codeService;

    /**
     * 邮箱验证码发送
     * 验证码使用场景：邮箱注册、邮箱登录、修改邮箱、注销账号
     * @param receiveEmail
     * @return
     */
    @GetMapping("/email/sendEmail")
    public ResponseResult sendSimpleMail(@RequestParam(value = "receiveEmail") String receiveEmail) {
        // 检查验证码是否发送过
        if(codeService.verifyEmailCode(receiveEmail) != null){
            return ResponseResult.success("验证码已发送，请勿重复发送");
        }

        codeService.sendEmailCode(receiveEmail);
        String message = String.format("验证码发送成功,有效时长%d秒", RedisConstants.CODE_EXPIRE);
        return ResponseResult.success(message);
    }

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
