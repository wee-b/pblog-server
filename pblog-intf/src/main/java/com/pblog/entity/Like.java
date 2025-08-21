package com.pblog.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户对文章的点赞记录表(Like)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:11:08
 */
public class Like implements Serializable {
    private static final long serialVersionUID = -76080602210820704L;
/**
     * 点赞记录ID，主键
     */
    private Long id;
/**
     * 用户ID，关联pb_user表
     */
    private Long userId;
/**
     * 文章ID，关联pb_article表
     */
    private Long articleId;
/**
     * 点赞状态：1-已点赞，0-取消点赞
     */
    private Integer isLiked;
/**
     * 点赞时间
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Integer getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Integer isLiked) {
        this.isLiked = isLiked;
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

