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
     * 用户账号（关联pb_user）
     */
    private String username;


    /**
     * 内容ID
     */
    private Integer targetId;
    /**
     * 内容类型：0-其他，1-文章，2-评论
     */
    private String targetType;

    /**
     * 点赞时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}