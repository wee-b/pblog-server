package com.pblog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.io.Serializable;

/**
 * 博客分类表，存储文章分类信息，支持多级分类(Category)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:09:59
 */
@TableName("pb_category")
public class Category implements Serializable {
    private static final long serialVersionUID = 757285295378306751L;
/**
     * 分类ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
/**
     * 分类名称
     */
    private String name;
/**
     * 分类别名（用于URL等场景）
     */
    private String alias;
/**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;
/**
     * 排序序号，数值越小越靠前
     */
    private Integer sortOrder;
/**
     * 分类描述
     */
    private String description;
/**
     * 分类状态：0-禁用，1-启用
     */
    private Integer status;
/**
     * 创建时间
     */
    private Date createdAt;
/**
     * 最后修改时间
     */
    private Date updatedAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

