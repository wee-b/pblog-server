package com.pblog.admin.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.pblog.common.constant.CaptchaConstants;
import com.pblog.common.vo.CaptchaVO;
import com.pblog.admin.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CodeServiceImpl implements CodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


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
