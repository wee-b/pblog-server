package com.pblog.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章点赞表
 * @TableName pb_like
 */
@TableName(value = "pb_like")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 点赞ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID（关联pb_user）
     */
    private Integer userId;

    /**
     * 文章ID（关联pb_article）
     */
    private Integer articleId;

    /**
     * 点赞状态（1已点赞 0取消）
     */
    private String isLiked;

    /**
     * 删除标志（0存在 1删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 点赞时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}