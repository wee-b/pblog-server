package com.pblog;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pblog.common.entity.User;
import com.pblog.user.PBlogApplication;
import com.pblog.user.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = PBlogApplication.class)
@RunWith(SpringRunner.class)
public class MyTest {

    @Autowired
    private UserMapper userMapper;



    @Test
    public void test() {
        System.out.println("test");
    }

    @Test
    public void TestBCryptPasswordEncoder(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123456"));

        System.out.println(encoder.matches("123456", "$2a$10$iHSX6ZaOKT4.R8eNgOa2QeTNvdaAwcIY5aSQ43oU5jKNxH.B3eDRG"));

    }

    @Test
    public void TestMapper(){
        System.out.println("userMapper 是否为 null：" + (userMapper == null));

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", "bin"); // eq 表示 =
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);
    }


}
