package com.pblog;

import com.pblog.user.PBlogApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = PBlogApplication.class)
@RunWith(SpringRunner.class)
public class GenerateRedisData {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void GeneratePermenentCode(){
        stringRedisTemplate.opsForValue().set("captcha:e1083e", "ab12");
    }
}
