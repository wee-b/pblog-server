package com.pblog.user.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.constant.RocketMQConstants;
import com.pblog.common.constant.TypeConstant;
import com.pblog.common.dto.LikeDTO;
import com.pblog.common.entity.Like;
import com.pblog.common.entity.LikeCount;
import com.pblog.user.mapper.LikeCountMapper;
import com.pblog.user.mapper.LikeMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 点赞消息消费者
 * 消费RocketMQ延迟消息，同步最终状态到数据库
 * 已移除参数校验（Controller层已完成）
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RocketMQConstants.LIKE_TOPIC, // 监听的主题
        consumerGroup = "${rocketmq.consumer.group}", // 消费者组（从配置文件读取）
        consumeThreadMax = 20, // 最大消费线程
        consumeTimeout = 30000 // 消费超时时间（毫秒）
)
public class LikeConsumer implements RocketMQListener<String> {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private LikeCountMapper likeCountMapper;

    /**
     * 消费消息核心方法
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务保证原子性
    public void onMessage(String msgBody) {
        try {
            log.info("handleLikeRequest consume time: {}", LocalDateTime.now());

            // 1. 反序列化消息
            LikeDTO dto = JSON.parseObject(msgBody, LikeDTO.class);

            String username = dto.getUsername();
            Integer targetId = dto.getTargetId();
            String targetType = dto.getTargetType();
            String operateType = dto.getOperateType();

            String uniqueKey = RocketMQConstants.LIKE_UNIQUE_KEY_PREFIX + username + ":" + targetType + ":" + targetId;
            String latestTimeStr = stringRedisTemplate.opsForValue().get(uniqueKey);
            if (latestTimeStr == null) {
                // TTL 过期或者被消费过，直接忽略
                return;
            }

            // 3. 比较消息时间和 Redis 最新时间
            if (!latestTimeStr.equals(dto.getTimeStrap())) {
                log.info("latestTimeStr:{}", latestTimeStr);
                log.info("dto.getTimeStrap():{}", dto.getTimeStrap());
                // 不是最新的消息，直接忽略
                return;
            }

            // 4. 同步点赞/取消点赞记录到数据库
            syncLikeRecordToDb(username, targetId, targetType, operateType);

            // 5. 同步点赞总数到数据库
            syncLikeCountToDb(targetId, targetType);

            log.info("消费点赞消息成功，用户：{}，目标：{}:{}，操作：{}", username, targetType, targetId, operateType);

        } catch (Exception e) {
            log.error("消费点赞消息失败，消息体：{}", msgBody, e);
            throw new RuntimeException("消费点赞消息异常", e); // 触发重试
        }
    }

    /**
     * 同步点赞/取消点赞记录到数据库
     */
    private void syncLikeRecordToDb(String username, Integer targetId, String targetType, String operateType) {
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<Like>()
                .eq(Like::getUsername, username)
                .eq(Like::getTargetId, targetId)
                .eq(Like::getTargetType, targetType);

        if (TypeConstant.LikeType.equals(operateType)) {
            // 防重插入（依赖数据库唯一索引）
            if (likeMapper.selectCount(queryWrapper) == 0) {
                Like likeEntity = new Like();
                likeEntity.setUsername(username);
                likeEntity.setTargetId(targetId);
                likeEntity.setTargetType(targetType);
                likeMapper.insert(likeEntity);
                log.info("新增点赞记录：{}:{}:{}", username, targetType, targetId);
            } else {
                log.warn("点赞记录已存在，无需重复插入：{}:{}:{}", username, targetType, targetId);
            }
        } else if(TypeConstant.UnlikeType.equals(operateType)){
            // 取消点赞：删除记录
            int deleteCount = likeMapper.delete(queryWrapper);
            if (deleteCount > 0) {
                log.info("删除点赞记录：{}:{}:{}", username, targetType, targetId);
            } else {
                log.warn("点赞记录不存在，无需删除：{}:{}:{}", username, targetType, targetId);
            }
        }
    }

    /**
     * 同步点赞总数到数据库
     */
    private void syncLikeCountToDb(Integer targetId, String targetType) {
        // 从Redis获取最新总数
        String countKey = RedisConstants.buildCountKey(targetType, targetId);
        String countStr = stringRedisTemplate.opsForValue().get(countKey);
        Integer total = countStr == null ? 0 : Integer.parseInt(countStr);

        // 查询总数记录是否存在
        LambdaQueryWrapper<LikeCount> countQueryWrapper = new LambdaQueryWrapper<LikeCount>()
                .eq(LikeCount::getTargetId, targetId)
                .eq(LikeCount::getTargetType, targetType);
        LikeCount existCount = likeCountMapper.selectOne(countQueryWrapper);

        if (existCount != null) {
            // 更新总数
            LambdaUpdateWrapper<LikeCount> updateWrapper = new LambdaUpdateWrapper<LikeCount>()
                    .eq(LikeCount::getTargetId, targetId)
                    .eq(LikeCount::getTargetType, targetType)
                    .set(LikeCount::getTotal, total);
            likeCountMapper.update(null, updateWrapper);
            log.info("更新点赞总数：{}:{}，总数：{}", targetType, targetId, total);
        } else {
            // 新增总数记录
            LikeCount likeCount = new LikeCount();
            likeCount.setTargetId(targetId);
            likeCount.setTargetType(targetType);
            likeCount.setTotal(total);
            likeCountMapper.insert(likeCount);
            log.info("新增点赞总数记录：{}:{}，总数：{}", targetType, targetId, total);
        }
    }
}