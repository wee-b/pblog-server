package com.pblog.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户信息表，存储系统所有用户的基本信息(User)实体类
 *
 * @author makejava
 * @since 2025-08-18 17:11:53
 */
public class User implements Serializable {
    private static final long serialVersionUID = 333020302798548236L;
/**
     * 用户ID，主键
     */
    private Long id;
/**
     * 用户名，登录账号
     */
    private String username;
/**
     * 密码，加密存储
     */
    private String password;
/**
     * 用户昵称
     */
    private String nickname;
/**
     * 用户邮箱
     */
    private String email;
/**
     * 用户头像URL
     */
    private String avatar;
/**
     * 用户角色：0-普通用户，1-管理员
     */
    private Integer role;
/**
     * 账号状态：0-禁用，1-正常
     */
    private Integer status;
/**
     * 用户简介
     */
    private String bio;
/**
     * 注册时间
     */
    private Date createdAt;
/**
     * 信息更新时间
     */
    private Date updatedAt;
/**
     * 最后登录时间
     */
    private Date lastLoginAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

}

