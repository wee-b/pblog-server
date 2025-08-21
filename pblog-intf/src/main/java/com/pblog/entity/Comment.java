package com.pblog.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 文章评论表，支持多级评论回复功能(Comment)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:10:19
 */
public class Comment implements Serializable {
    private static final long serialVersionUID = 720016341333076619L;
/**
     * 评论ID，主键
     */
    private Long id;
/**
     * 文章ID，关联pb_article表
     */
    private Long articleId;
/**
     * 评论用户ID，关联pb_user表
     */
    private Long userId;
/**
     * 父评论ID，自关联，NULL表示一级评论
     */
    private Long parentId;
/**
     * 评论内容，最长1000字符
     */
    private String content;
/**
     * 评论状态：0-待审核，1-已通过，2-已驳回
     */
    private Integer status;
/**
     * 评论点赞数
     */
    private Integer likeCount;
/**
     * 评论时间
     */
    private Date createdAt;
/**
     * 状态更新时间
     */
    private Date updatedAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}

