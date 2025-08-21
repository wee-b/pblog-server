package com.pblog.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 文章系列表，用于将多篇文章归类为一个系列(Series)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:11:42
 */
public class Series implements Serializable {
    private static final long serialVersionUID = -57424282371224023L;
/**
     * 系列ID，主键
     */
    private Long id;
/**
     * 系列标题
     */
    private String title;
/**
     * 系列描述
     */
    private String description;
/**
     * 系列封面图片URL
     */
    private String coverImage;
/**
     * 创建者ID，关联pb_user表
     */
    private Long authorId;
/**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;
/**
     * 系列排序，值越小越靠前
     */
    private Integer sortOrder;
/**
     * 包含的文章数量
     */
    private Integer articleCount;
/**
     * 创建时间
     */
    private Date createdAt;
/**
     * 更新时间
     */
    private Date updatedAt;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
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

