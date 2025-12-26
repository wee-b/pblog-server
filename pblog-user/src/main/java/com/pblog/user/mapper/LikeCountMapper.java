package com.pblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.LikeCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LikeCountMapper extends BaseMapper<LikeCount> {
    /**
     * 自定义更新点赞总数
     */
    @Update("UPDATE pb_like_counts SET total = #{total} WHERE target_id = #{targetId} AND target_type = #{targetType}")
    int updateCount(@Param("targetId") Integer targetId, @Param("targetType") String targetType, @Param("total") Integer total);
}
