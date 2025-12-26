package com.pblog.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.constant.RocketMQConstants;
import com.pblog.common.constant.TypeConstant;
import com.pblog.common.dto.LikeDTO;
import com.pblog.common.entity.Like;
import com.pblog.common.entity.LikeCount;
import com.pblog.common.utils.SecurityContextUtil;
import com.pblog.user.mapper.LikeCountMapper;
import com.pblog.user.mapper.LikeMapper;
import com.pblog.user.service.LikeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private LikeMapper likeMapper;
    @Resource
    private LikeCountMapper likeCountMapper;


    // Lua脚本
    private final RedisScript<Long> likeScript;
    private final RedisScript<Long> cancelLikeScript;

    public LikeServiceImpl() {
        this.likeScript = new DefaultRedisScript<>(loadLuaScript("lua/like.lua"), Long.class);
        this.cancelLikeScript = new DefaultRedisScript<>(loadLuaScript("lua/cancelLike.lua"), Long.class);
    }

    private String loadLuaScript(String path) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            return new String(resolver.getResource(path).getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("加载Lua脚本失败", e);
        }
    }

    @Override
    public void handleLikeRequest(LikeDTO dto) {
        // 1. 获取用户名并填充DTO
        String username = SecurityContextUtil.getUsername();
        dto.setUsername(username);

        String targetType = dto.getTargetType();
        Integer targetId = dto.getTargetId();

        // 2. 执行Lua脚本更新Redis
        Long result = executeLuaScript(dto);
        if (result != 1) {
            return;
        }
        log.info("更新缓存");

        // 3. 生成唯一Key
        String uniqueKey = RocketMQConstants.LIKE_UNIQUE_KEY_PREFIX + username + ":" + targetType + ":" + targetId;
        String time = String.valueOf(System.currentTimeMillis());
        dto.setTimeStrap(time);

        // 存入最新时间戳
        stringRedisTemplate.opsForValue().set(
                uniqueKey,
                time,
                RocketMQConstants.LIKE_DELAY_TIME, TimeUnit.MINUTES
        );

        // 4. 构建消息（修正：用字符串"DELAY"替代DELAY_TIME_LEVEL常量）
        String msgBody = JSON.toJSONString(dto);
        Message<String> message = MessageBuilder.withPayload(msgBody)
                .setHeader(RocketMQHeaders.KEYS, uniqueKey)
                .build();

        // 5. 发送消息
        sendRocketMQMessage(message);
    }

    private Long executeLuaScript(LikeDTO dto) {
        String userKey = RedisConstants.buildUserLikeKey(dto.getTargetType(), dto.getTargetId());
        String countKey = RedisConstants.buildCountKey(dto.getTargetType(), dto.getTargetId());
        List<String> keys = List.of(userKey, countKey);

        if (TypeConstant.LikeType.equals(dto.getOperateType())) {
            return stringRedisTemplate.execute(likeScript, keys, dto.getUsername());
        } else {
            return stringRedisTemplate.execute(cancelLikeScript, keys, dto.getUsername());
        }
    }

    private void sendRocketMQMessage(Message<String> message) {
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(
                    RocketMQConstants.LIKE_TOPIC,
                    message,
                    3000,
                    RocketMQConstants.LIKE_DELAY_LEVEL
            );
            log.info("handleLikeRequest send time: {}", LocalDateTime.now());
//            log.info("生产者发送了一条消息{}", sendResult);
            // 核心修正：明确导入SendStatus枚举，并正确判断
            if (sendResult.getSendStatus() != org.apache.rocketmq.client.producer.SendStatus.SEND_OK) {
                throw new RuntimeException("发送点赞消息失败，SendStatus：" + sendResult.getSendStatus());
            }
        } catch (Exception e) {
            throw new RuntimeException("发送RocketMQ消息异常", e);
        }
    }

    @Override
    public boolean isLiked(String targetType, Integer targetId) {
        String username = SecurityContextUtil.getUsername();
        String userKey = RedisConstants.buildUserLikeKey(targetType, targetId);
        // 1.查询缓存，缓存有就直接返回
        if(stringRedisTemplate.hasKey(userKey)){
            return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(userKey, username));
        }
        // 2.缓存中没有就查询数据库，同时将数据写入缓存
        // 查询like表
        QueryWrapper<Like> likeQueryWrapper = new QueryWrapper<>();
        likeQueryWrapper.eq("target_type", targetType);
        likeQueryWrapper.eq("target_id", targetId);
        List<Like> likes = likeMapper.selectList(likeQueryWrapper);

        // 构建缓存
        if (likes != null && !likes.isEmpty()) {
            String[] likeUsernames = likes.stream()
                    .map(Like::getUsername)
                    .filter(name -> name != null && !name.isBlank())
                    .toArray(String[]::new);
            // 批量添加到Redis集合
            stringRedisTemplate.opsForSet().add(userKey, likeUsernames);
            // 设置缓存过期时间
            stringRedisTemplate.expire(userKey, RedisConstants.Like_Set_TTL, TimeUnit.MINUTES);
        }

        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(userKey, username));
    }

    @Override
    public Integer getLikeCount(String targetType, Integer targetId) {
        String countKey = RedisConstants.buildCountKey(targetType, targetId);
        // 1.查询缓存，缓存有就直接返回
        if(stringRedisTemplate.hasKey(countKey)){
            String count = stringRedisTemplate.opsForValue().get(countKey);
            return count == null ? 0 : Integer.parseInt(count);
        }
        // 2.缓存中没有就查询数据库，同时将数据写入缓存
        QueryWrapper<LikeCount> QueryWrapper = new QueryWrapper<>();
        QueryWrapper.eq("target_type", targetType);
        QueryWrapper.eq("target_id", targetId);
        LikeCount lc = likeCountMapper.selectOne(QueryWrapper);

        String count = lc.getTotal().toString();

        stringRedisTemplate.opsForValue().set(countKey, count);
        stringRedisTemplate.expire(countKey, RedisConstants.Like_Set_TTL, TimeUnit.MINUTES);

        return count == null ? 0 : Integer.parseInt(count);
    }
}