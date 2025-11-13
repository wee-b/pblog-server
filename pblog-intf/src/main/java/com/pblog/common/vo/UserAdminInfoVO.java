package com.pblog.common.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAdminInfoVO {

    public String username;
    public String nickname;
    public String avatarUrl;
    public String email;


    public LocalDateTime lastLoginTime;
    public LocalDateTime createTime;
    public String status;
    public String remark;
}

