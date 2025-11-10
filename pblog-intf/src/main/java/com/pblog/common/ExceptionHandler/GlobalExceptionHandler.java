package com.pblog.common.ExceptionHandler;

import com.alibaba.fastjson.JSON;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.utils.WebUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;

/**
 * 全局异常处理
 * spring+security项目中的异常
 * 1、security中的两大异常授权异常(AccessDeniedException)+认证异常(AuthenticationException)交由security自己处理
 * 2、其他异常自己处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //    仅做 “透传” 不做响应封装，确保 Security 原生逻辑生效
    //security的授AccessDeniedException及子类)抛出交由security AuthenticationEntryPoint 处理
    @ExceptionHandler(AccessDeniedException.class)
    public void accessDeniedException(AccessDeniedException e) throws AccessDeniedException {
        throw e;
    }

    //security的认证异常(AuthenticationException及子类)抛出由security AccessDeniedHandler 处理
    @ExceptionHandler(AuthenticationException.class)
    public void authenticationException(AuthenticationException e) throws AuthenticationException {
        throw e;
    }

    // -------------------------- 1. 处理自定义业务异常 --------------------------
    @ExceptionHandler(BusinessException.class)
    public ResponseResult handleBusinessException(BusinessException e, HttpServletResponse response) {
        log.info("业务异常：{}", e.getMessage());
        // 使用ResponseResult.error(String message)，默认code=400
        return ResponseResult.error(e.getMessage());
    }


    // -------------------------- 2. 处理框架/参数校验异常 --------------------------

    /**
     * 参数校验异常（@Valid注解触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletResponse response) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError firstError = bindingResult.getFieldError();
        String message = firstError != null ? firstError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验异常：{}", message);

        // 使用默认错误code=400
        return ResponseResult.error(400, message);

    }

    /**
     * 请求方法不支持（如GET访问POST接口）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletResponse response) {
        String supportedMethods = String.join(",", e.getSupportedMethods() != null ? e.getSupportedMethods() : new String[0]);
        String message = String.format("不支持%s请求方法，支持的方法：%s", e.getMethod(), supportedMethods);
        log.warn("请求方法异常：{}", message);

        // 使用自定义code=405（Method Not Allowed）
        return ResponseResult.error(405, message);
    }

    /**
     * 接口不存在（404）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseResult handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletResponse response) {
        String message = "请求的接口不存在：" + e.getRequestURL();
        log.warn("接口不存在：{}", message);

        // 使用自定义code=404（Not Found）
        return ResponseResult.error(404, message);
    }

    /**
     * 参数类型不匹配（如前端传字符串，后端需数字）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletResponse response) {
        String message = String.format("参数[%s]类型不匹配，期望类型：%s",
                e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型异常：{}", message);

        return ResponseResult.error(400, message);
    }


    // -------------------------- 3. 处理系统异常 --------------------------

    /**
     * 数据库异常
     */
    @ExceptionHandler(SQLException.class)
    public void handleSQLException(SQLException e, HttpServletResponse response) {
        log.error("数据库异常", e); // 记录详细堆栈
        // 前端显示友好提示，不暴露具体错误
        ResponseResult<Void> result = ResponseResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "数据库操作失败，请稍后重试");
        String jsonString = JSON.toJSONString(result);
        WebUtils.renderString(response, jsonString);
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public void handleNullPointerException(NullPointerException e, HttpServletResponse response) {
        log.error("空指针异常", e);
        ResponseResult<Void> result = ResponseResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请稍后重试");
        String jsonString = JSON.toJSONString(result);
        WebUtils.renderString(response, jsonString);
    }

    /**
     * 兜底异常（捕获所有未处理的异常）
     */
    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletResponse response) {
        // 关键：打印异常全类名+完整堆栈，明确未处理的异常类型
        log.error("未处理的异常类型：{}，异常信息：{}",
                e.getClass().getName(), e.getMessage(), e);
        ResponseResult<Void> result = ResponseResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请稍后重试");
        String jsonString = JSON.toJSONString(result);
        WebUtils.renderString(response, jsonString);
    }
}