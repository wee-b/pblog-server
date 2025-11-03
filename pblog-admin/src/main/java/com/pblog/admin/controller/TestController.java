package com.pblog.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/hello")
//    @PreAuthorize("hasAnyRole('admin')")
    public String hello() {
        System.out.println("=== /hello Controller 被调用 ==="); // 打印日志确认
        return "hello ";
    }
}
