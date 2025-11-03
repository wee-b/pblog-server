package com.pblog.common.ExceptionHandler;

import com.alibaba.fastjson.JSON;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证失败异常处理器（区分用户名不存在和密码错误）
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 定义响应结果和消息
        ResponseResult responseResult;
        String message;

        // 判断异常类型，返回不同消息
        if (authException instanceof UsernameNotFoundException) {
            // 用户名不存在：提示“用户名不存在”
            message = "用户名不存在";
            log.info("认证失败：{}", message);
        } else if (authException instanceof BadCredentialsException) {
            // 密码错误：提示“密码错误”
            message = "密码错误";
            log.info("认证失败：{}", message);
        } else {
            // 其他认证异常（如账号锁定、禁用等）：默认提示
            message = "用户认证失败，请重新登录";
            log.info("认证异常：{}，详情：{}", message, authException.getMessage());
        }

        // 构建响应结果（状态码统一用 401 UNAUTHORIZED）
        responseResult = new ResponseResult(HttpStatus.UNAUTHORIZED.value(), message);
        // 转换为JSON并返回
        String jsonString = JSON.toJSONString(responseResult);
        WebUtils.renderString(response, jsonString);
    }
}