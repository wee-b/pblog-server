package com.pblog.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 博客文章表，存储所有文章的基本信息(Article)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:08:29
 */
public class Article implements Serializable {
    private static final long serialVersionUID = -62088933904006223L;
/**
     * 文章ID，主键
     */
    private Long id;
/**
     * 文章标题，最长200字符
     */
    private String title;
/**
     * 文章正文内容
     */
    private String content;
/**
     * 文章摘要，可选，最长500字符
     */
    private String summary;
/**
     * 文章封面图片URL
     */
    private String coverImage;
/**
     * 作者ID，关联user表的id
     */
    private Long authorId;
/**
     * 文章状态：0-草稿，1-已发布，2-已下架
     */
    private Integer status;
/**
     * 阅读量，初始为0
     */
    private Integer viewCount;
/**
     * 点赞数，初始为0
     */
    private Integer likeCount;
/**
     * 评论数，初始为0
     */
    private Integer commentCount;
/**
     * 是否置顶：0-否，1-是
     */
    private Integer isSticky;
/**
     * 是否推荐：0-否，1-是
     */
    private Integer isFeatured;
/**
     * 创建时间（首次保存时间）
     */
    private Date createdAt;
/**
     * 最后修改时间
     */
    private Date updatedAt;
/**
     * 发布时间（状态改为已发布时更新）
     */
    private Date publishedAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getIsSticky() {
        return isSticky;
    }

    public void setIsSticky(Integer isSticky) {
        this.isSticky = isSticky;
    }

    public Integer getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Integer isFeatured) {
        this.isFeatured = isFeatured;
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

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

}

