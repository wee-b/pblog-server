package com.pblog.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pblog.common.dto.LoginUser;
import com.pblog.common.entity.User;
import com.pblog.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 如果没有查询到用户信息就抛出异常
        if(Objects.isNull(user)){
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        // TODO 查询对应的权限信息
        log.info("查询权限中");
        List<String> list = new ArrayList<>(Arrays.asList("test","user","admin"));

        return new LoginUser(user,list);
    }
}
