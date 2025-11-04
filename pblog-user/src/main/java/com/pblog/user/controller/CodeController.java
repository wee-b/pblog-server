package com.pblog.user.controller;

import com.pblog.common.constant.RedisConstants;
import com.pblog.common.result.ResponseResult;

import com.pblog.user.service.CodeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
