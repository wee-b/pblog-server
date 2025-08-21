package com.pblog.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 系列与文章的关联表，实现多对多关系(SaRelation)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:11:25
 */
public class SaRelation implements Serializable {
    private static final long serialVersionUID = -85311938208659906L;
/**
     * 关联记录ID，主键
     */
    private Long id;
/**
     * 系列ID，关联pb_series表
     */
    private Long seriesId;
/**
     * 文章ID，关联pb_article表
     */
    private Long articleId;
/**
     * 文章在系列中的排序位置，值越小越靠前
     */
    private Integer sortOrder;
/**
     * 添加时间
     */
    private Date addedAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

}

