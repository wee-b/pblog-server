package com.pblog.common.ExceptionHandler;

/**
 * 自定义业务异常类：用于标识已知的业务错误（如“邮箱已被注册”“订单不存在”等）
 */
public class BusinessException extends RuntimeException {

    // 无参构造
    public BusinessException() {
        super();
    }

    // 带错误消息的构造（最常用）
    public BusinessException(String message) {
        super(message); // 调用父类RuntimeException的构造，传入错误消息
    }

    // 带错误消息和异常原因的构造（用于需要追溯异常链的场景）
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
