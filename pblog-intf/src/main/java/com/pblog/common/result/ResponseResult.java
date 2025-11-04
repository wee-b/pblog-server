package com.pblog.common.result;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResponseResult<T> {
    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // 无参构造（必须存在，否则JSON序列化可能出问题）
    public ResponseResult() {
        this.timestamp = LocalDateTime.now();
    }

    // 新增：接收code和message的构造方法
    public ResponseResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // 新增：全参构造
    public ResponseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 静态工厂方法（推荐方式）
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(200, "操作成功");
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "操作成功", data);
    }

    public static <T> ResponseResult<T> error(int code, String message) {
        return new ResponseResult<>(code, message);
    }

    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(400, message);
    }
}
