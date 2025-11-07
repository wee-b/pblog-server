package com.pblog.user.controller;

import com.pblog.common.dto.*;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.vo.UserInfoVO;
import com.pblog.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    /**
     * 邮箱验证码登录
     */
    @PostMapping("/emailCodeLogin")
    public ResponseResult emailCodeLogin(@RequestBody EmailCodeDTO emailCodeDTO) throws Exception {
        log.info("收到邮箱验证码登录请求:{}", emailCodeDTO);
        Map<String,String> map =  userService.emailCodeLogin(emailCodeDTO);
        return ResponseResult.success(map);
    }

    /**
     * 注册可以有邮箱也可以没有邮箱，没有邮箱视为游客
     * @param registerDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody RegisterDTO registerDTO) throws Exception {
        String username = userService.register(registerDTO);
        return ResponseResult.success(username);
    }

    /**
     * 重置密码，必须要有邮箱，未登陆状态下
     */
    @PutMapping("/resetPassword")
    public ResponseResult resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws Exception {
        String username = userService.resetPassword(resetPasswordDTO);
        return ResponseResult.success(username);
    }

    // ========================  登录状态下的请求  ========================
    /**
     * 修改用户基本信息,登录状态下
     */
    @PutMapping("/updateInfo")
    public ResponseResult updateInfo(@RequestBody UserDTO userDTO) throws Exception {
        String username = userService.updateInfo(userDTO);
        return ResponseResult.success(username);
    }

    /**
     * 修改/添加邮箱,登录状态下
     */
    @PutMapping("/updateEmail")
    public ResponseResult updateEmail(@RequestBody EmailCodeDTO emailCodeDTO) throws Exception {
        String username = userService.updateEmail(emailCodeDTO);
        return ResponseResult.success(username);
    }

    /**
     * 修改密码,登陆状态下,前端传入明文密码
     */
    @PutMapping("/forgetPassword")
    public ResponseResult resetPassword(@RequestParam("newPassword")  String newPassword) throws Exception {
        String username = userService.forgetPassword(newPassword);
        return ResponseResult.success(username);
    }

    /**
     * 账号注销
     */
    @PostMapping("/logout")
    public ResponseResult logout() throws Exception {
        String res = userService.logout();
        return ResponseResult.success(res);
    }

    /**
     * 账号注销
     */
    @DeleteMapping("/deleteAccount")
    public ResponseResult deleteAccount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String res = userService.deleteAccount(request,response);
        return ResponseResult.success(res);
    }

    /**
     * 获取用户资料
     */
    @GetMapping("/getUserInfo")
    public ResponseResult<UserInfoVO> getUserInfo() throws Exception {
        UserInfoVO userInfo = userService.getUserInfo();
        return ResponseResult.success(userInfo);
    }

}
