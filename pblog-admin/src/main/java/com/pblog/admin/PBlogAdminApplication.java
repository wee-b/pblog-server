package com.pblog.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication()
@ComponentScan(basePackages = {
        "com.pblog.admin",        // 项目主包
        "com.pblog.common"   // 配置类所在包
})
public class PBlogAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(PBlogAdminApplication.class, args);
    }
}
