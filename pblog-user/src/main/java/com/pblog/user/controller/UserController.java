package com.pblog.user.controller;

import com.pblog.common.dto.PasswordLoginDTO;
import com.pblog.common.result.ResponseResult;
import com.pblog.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/passwordLogin")
    public ResponseResult<Map<String,String>> login(@RequestBody PasswordLoginDTO passwordLoginDTO) throws Exception {
        log.info("收到密码登录请求:{}", passwordLoginDTO);
        Map<String,String> map =  userService.login(passwordLoginDTO);
        return ResponseResult.success(map);
    }
}
