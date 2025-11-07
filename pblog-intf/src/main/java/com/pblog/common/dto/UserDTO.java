package com.pblog.common.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {


    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 用户简介
     */
    private String bio;

}
