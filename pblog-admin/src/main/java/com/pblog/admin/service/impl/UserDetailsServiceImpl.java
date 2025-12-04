package com.pblog.admin.service.impl;

import com.pblog.admin.mapper.UserMapper;
import com.pblog.admin.mapper.UserRoleMapper;
import com.pblog.common.dto.LoginUser;
import com.pblog.common.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {

        log.info("loadUserByUsername");
        // 查询用户信息
        Integer userId = Integer.parseInt(userid);
        User user = userMapper.selectById(userId);

        // 如果没有查询到用户信息就抛出异常
        if(Objects.isNull(user)|| user.getDelFlag().equals("1")){
            // TODO 报错不走ExceptionHandler
            throw new UsernameNotFoundException("用户不存在");
        }
        if(user.getStatus().equals("1")){
            throw new DisabledException("账号已被禁用");
        }

        // 查询对应的权限信息
        List<String> lis = userRoleMapper.selectRoleKeysByUserId(userId);
        log.info("userId:{}用户的权限{}",userId,lis);

        return new LoginUser(user,lis);
    }
}
