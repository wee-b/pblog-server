package com.pblog.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客文章表
 * @TableName pb_article
 */
@TableName(value = "pb_article")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章正文
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章封面URL
     */
    private String coverImage;

    /**
     * 作者ID（关联pb_user）
     */
    @TableField("author_username")
    private String authorUsername;
    @TableField("author_nickname")
    private String authorNickName;

    /**
     * 文章状态（0草稿 1已发布 2待审核）
     */
    private String status;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 是否置顶（0否 1是）
     */
    private String sticky;

    // TODO 设置定时任务
    /**
     * 是否推荐（0否 1是）
     */
    private String featured;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 删除标志（0存在 1删除）
     */
//    @TableLogic
    private String delFlag;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.UPDATE)
    private Integer updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
}