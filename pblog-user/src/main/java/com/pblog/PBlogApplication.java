package com.pblog;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@MapperScan("com.pblog.mapper")
public class PBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(PBlogApplication.class, args);
    }
}
