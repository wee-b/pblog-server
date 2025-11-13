package com.pblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pblog.admin.mapper.UserMapper;
import com.pblog.common.dto.LoginUser;
import com.pblog.common.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername");
        // 查询用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", username);
        User user = userMapper.selectOne(queryWrapper);

        // 如果没有查询到用户信息就抛出异常
        if(Objects.isNull(user)|| user.getDelFlag().equals("1")){
            // TODO 报错不走ExceptionHandler
            throw new UsernameNotFoundException("用户不存在");
        }

//        log.info("user.getStatus():" + user.getStatus());
        if(user.getStatus().equals("0")){
            throw new DisabledException("账号已被禁用");
        }

        // TODO 查询对应的权限信息
        log.info("查询权限中");
        List<String> list = new ArrayList<>(Arrays.asList("test","user","admin"));

        return new LoginUser(user,list);
    }
}
