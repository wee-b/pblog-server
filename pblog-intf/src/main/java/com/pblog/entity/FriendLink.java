package com.pblog.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 博客友情链接表，管理所有友情链接信息(FriendLink)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:10:41
 */
public class FriendLink implements Serializable {
    private static final long serialVersionUID = 678836755648712261L;
/**
     * 友链ID，主键
     */
    private Long id;
/**
     * 友链网站名称
     */
    private String siteName;
/**
     * 友链网站URL地址
     */
    private String siteUrl;
/**
     * 友链网站logo图片URL
     */
    private String logo;
/**
     * 友链网站简介
     */
    private String description;
/**
     * 联系人邮箱，用于沟通
     */
    private String contactEmail;
/**
     * 友链状态：0-待审核，1-已通过，2-已拒绝
     */
    private Integer status;
/**
     * 展示排序，值越小越靠前
     */
    private Integer sortOrder;
/**
     * 申请时间
     */
    private Date createdAt;
/**
     * 状态更新时间
     */
    private Date updatedAt;
/**
     * 审核人ID，关联pb_user表
     */
    private Long approvedBy;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
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

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

}

