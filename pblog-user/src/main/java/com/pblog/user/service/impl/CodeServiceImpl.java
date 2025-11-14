package com.pblog.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pblog.common.enumeration.EmailConstants;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.entity.User;
import com.pblog.common.utils.RandomCodeUtil;
import com.pblog.user.mapper.UserMapper;
import com.pblog.user.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CodeServiceImpl implements CodeService {
    // 注入JavaMailSender接口
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    // 通过value注解得到配置文件中发送者的邮箱
    @Value("${spring.mail.username}")
    private String userName;// 用户发送者


    public boolean verifyEmailUser(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User user = userMapper.selectOne(queryWrapper);

        return user == null;
    }

    public String verifyEmailCode(String email) {

        String s = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_EmailCode_KEY + email);
        return s;
    }

    //
    public void sendEmailCode(String email){

        try{
            // 定义email信息格式
            SimpleMailMessage message = new SimpleMailMessage();
            // 生成验证码
            String code = RandomCodeUtil.generate();

            // 保存到redis
            String key = RedisConstants.LOGIN_EmailCode_KEY + email;
            int expireTime = RedisConstants.CODE_EXPIRE;
            stringRedisTemplate.opsForValue().set(key, code,expireTime, TimeUnit.SECONDS);

            // 设置发件人
            message.setFrom(userName);
            // 接收者邮箱，为调用本方法传入的接收者的邮箱xxx@qq.com
            message.setTo(email);
            // 邮件主题
            message.setSubject(EmailConstants.EMAIL_TITLE.getValue());
            // 邮件内容  设置的邮件内容，这里我使用了常量类字符串，加上验证码，再加上常量类字符串
            message.setText(EmailConstants.EMAIL_MESSAGE.getValue()+code+EmailConstants.EMAIL_OUTTIME_TEN.getValue());
            // 开始发送
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
