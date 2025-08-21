package com.pblog.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 文章与分类的关联表，实现文章多分类功能(AcRelation)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:05:38
 */
public class AcRelation implements Serializable {
    private static final long serialVersionUID = -77845269402404967L;
/**
     * 关联记录ID，主键
     */
    private Long id;
/**
     * 文章ID，关联pb_article表
     */
    private Long articleId;
/**
     * 分类ID，关联pb_category表
     */
    private Long categoryId;
/**
     * 关联创建时间
     */
    private Date createdAt;


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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}

