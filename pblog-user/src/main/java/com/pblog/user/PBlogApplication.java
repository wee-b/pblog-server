package com.pblog.user;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication()
@ComponentScan(basePackages = {
        "com.pblog.user",          // 项目主包
        "com.pblog.common"   // 配置类所在包
})
public class PBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(PBlogApplication.class, args);
    }
}
