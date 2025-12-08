package com.pblog.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章评论表
 * @TableName pb_comment
 */
@TableName(value = "pb_comment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 评论ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文章ID（关联pb_article）
     * 文章id=0代表为留言板数据
     */
    private Integer articleId;

    /**
     * 评论用户账号（关联pb_user）
     */
    private String username;

    /**
     * 根评论ID（自关联）
     */
    private Integer rootId;

    /**
     * 父评论ID（自关联）
     */
    private Integer parentId;

    /**
     * 发布父评论的人的账号
     */
    private String toReplyUsername;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论状态（0待审核 1已通过 2已驳回）
     */
    private String status;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 删除标志（0存在 1删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 评论时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}