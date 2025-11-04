package com.pblog.user.controller;

import com.pblog.common.dto.EmailCodeLoginDTO;
import com.pblog.common.dto.EmailLoginDTO;
import com.pblog.common.dto.PasswordLoginDTO;
import com.pblog.common.dto.RegisterDTO;
import com.pblog.common.result.ResponseResult;
import com.pblog.user.service.CodeService;
import com.pblog.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CodeService codeService;

    @PostMapping("/passwordLogin")
    public ResponseResult<Map<String,String>> passwordLogin(@RequestBody PasswordLoginDTO passwordLoginDTO) throws Exception {
        log.info("收到密码登录请求:{}", passwordLoginDTO);
        Map<String,String> map =  userService.passwordLogin(passwordLoginDTO);
        return ResponseResult.success(map);
    }


    /**
     * 邮箱登录
     */
    @PostMapping("/emailLogin")
    public ResponseResult<Map<String,String>> emailLogin(@RequestBody EmailLoginDTO emailLoginDTO) throws Exception {
        log.info("收到邮箱密码登录请求:{}", emailLoginDTO);
        Map<String,String> map =  userService.emailLogin(emailLoginDTO);
        return ResponseResult.success(map);
    }


    @PostMapping("/emailCodeLogin")
    public ResponseResult emailCodeLogin(@RequestBody EmailCodeLoginDTO emailCodeLoginDTO) throws Exception {
        log.info("收到邮箱验证码登录请求:{}", emailCodeLoginDTO);
        Map<String,String> map =  userService.emailCodeLogin(emailCodeLoginDTO);
        return ResponseResult.success(map);
    }

    /**
     * 注册可以有邮箱也可以没有邮箱
     * @param registerDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody RegisterDTO registerDTO) throws Exception {
        String username = userService.register(registerDTO);
        return ResponseResult.success(username);
    }

}
