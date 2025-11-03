package com.pblog.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @RequestMapping("/hello")
//    @PreAuthorize("hasAnyRole('admin')")
    public String hello() {
        log.info("=== /hello Controller 被调用 ===");
        return "hello ";
    }
}
