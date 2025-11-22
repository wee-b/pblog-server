package com.pblog.user.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pblog.common.Expection.BusinessException;
import com.pblog.common.constant.CaptchaConstants;
import com.pblog.common.enumeration.EmailConstants;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.entity.User;
import com.pblog.common.utils.RandomCodeUtil;
import com.pblog.common.vo.CaptchaVO;
import com.pblog.user.mapper.UserMapper;
import com.pblog.user.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CodeServiceImpl implements CodeService {
    // 注入JavaMailSender接口
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    // 通过value注解得到配置文件中发送者的邮箱
    @Value("${spring.mail.username}")
    private String userName;// 用户发送者



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

    @Override
    public void verifyEmailCode(String key, String code) throws BusinessException {
        // 迁移自 UserServiceImpl 的校验逻辑（优化命名和异常提示）
        String realCode = stringRedisTemplate.opsForValue().get(key);

        // 1. 校验验证码是否存在（已过期）
        if (realCode == null) {
            throw new BusinessException("邮箱验证码已过期，请重新获取");
        }

        // 2. 校验验证码是否正确（注意：如果生成时是数字，建议忽略大小写（如果是字母））
        if (!realCode.equals(code.trim())) { // trim() 去除用户输入的空格
            throw new BusinessException("邮箱验证码错误，请重新输入");
        }

        // 3. （可选）校验通过后删除 Redis 中的验证码（防止重复使用）
        // TODO 暂时不删除验证码
//        stringRedisTemplate.delete(key);
    }


    @Override
    public CaptchaVO generateCaptcha() {
        // 1. 使用 Hutool 创建线段干扰的验证码（可替换为圆圈干扰、中文验证码等）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(
                CaptchaConstants.CAPTCHA_WIDTH,
                CaptchaConstants.CAPTCHA_HEIGHT,
                CaptchaConstants.CAPTCHA_LENGTH,
                10 // 干扰线数量
        );

        // 2. 生成唯一 UUID 作为验证码的 key（用于后续校验）
        String captchaUuid = UUID.randomUUID().toString().replace("-", "");

        // 3. 将验证码内容（小写）存入 Redis，设置过期时间
        String captchaCode = captcha.getCode().toLowerCase();
        String redisKey = CaptchaConstants.CAPTCHA_REDIS_KEY_PREFIX + captchaUuid;
        stringRedisTemplate.opsForValue().set(redisKey, captchaCode, CaptchaConstants.CAPTCHA_EXPIRE_SECONDS, java.util.concurrent.TimeUnit.SECONDS);

        // 4. 封装到自定义 DTO 返回
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaUuid(captchaUuid);
        captchaVO.setCaptcha(captcha);
        return captchaVO;
    }

    /**
     * 校验验证码（前端传入 UUID + 用户输入的验证码）
     * @param captchaUuid 验证码唯一标识（生成时返回的 UUID）
     * @param userInputCode 用户输入的验证码（前端传入）
     * @return 校验结果（true=通过，false=失败）
     */
    @Override
    public boolean verifyCaptcha(String captchaUuid, String userInputCode) {
        // 1. 参数校验
        if (captchaUuid == null || captchaUuid.isEmpty() || userInputCode == null || userInputCode.isEmpty()) {
            return false;
        }

        // 2. 拼接 Redis key，获取存储的验证码
        String redisKey = CaptchaConstants.CAPTCHA_REDIS_KEY_PREFIX + captchaUuid;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) {
            return false; // 验证码已过期或不存在
        }

        // 3. 忽略大小写校验（前端输入可能大小写混合）
        boolean isMatch = storedCode.equals(userInputCode.toLowerCase());

        // 4. 校验通过后删除 Redis 中的验证码（防止重复使用）
        // TODO 暂时不删除验证码
//        if (isMatch) {
//            stringRedisTemplate.delete(redisKey);
//        }

        return isMatch;
    }
}
