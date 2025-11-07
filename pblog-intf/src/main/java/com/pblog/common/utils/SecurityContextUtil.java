package com.pblog.common.utils;

import com.pblog.common.dto.LoginUser;
import com.pblog.common.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityContextUtil {

    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("SecurityContextUtil:getLoginUser:"+authentication);
        if (authentication == null) {
            throw new RuntimeException("未获取到认证信息");
        }
        if (authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        throw new RuntimeException("用户信息格式错误");
    }

    /**
     * 获取当前登录用户ID
     */
    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    public static User getUser() {
        return getLoginUser().getUser();
    }




}
