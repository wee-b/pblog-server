package com.pblog.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper extends BaseMapper<Like> {
    /**
     * 自定义删除点赞记录（取消点赞）
     */
    @Delete("DELETE FROM pb_like WHERE username = #{username} AND target_id = #{targetId} AND target_type = #{targetType}")
    int deleteLike(@Param("username") String username, @Param("targetId") Integer targetId, @Param("targetType") String targetType);
}
