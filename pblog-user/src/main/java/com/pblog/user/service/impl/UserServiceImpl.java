package com.pblog.user.service.impl;



import com.pblog.common.constant.RedisConstants;
import com.pblog.common.dto.LoginUser;
import com.pblog.common.dto.PasswordLoginDTO;
import com.pblog.common.entity.User;
import com.pblog.user.service.UserService;
import com.pblog.common.utils.JjwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    // ✅ 注入 UserDetailsService（复用用户查询逻辑）
    @Autowired
    private UserDetailsService userDetailsService;

    // ✅ 注入 PasswordEncoder（手动校验密码）
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Map<String, String> login(PasswordLoginDTO passwordLoginDTO) throws BadCredentialsException {
        Map<String, String> map = new HashMap<>();

        // 1. 手动查询用户信息（复用 UserDetailsService 的逻辑，避免重复代码）
        UserDetails userDetails = userDetailsService.loadUserByUsername(passwordLoginDTO.getUsername());
        LoginUser loginUser = (LoginUser) userDetails;
        User user = loginUser.getUser();

        // 2. 手动校验密码（与 SecurityConfig 中配置的 PasswordEncoder 一致）
        if (!passwordEncoder.matches(passwordLoginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }


        // 3. 手动创建认证对象，存入 SecurityContext（替代 authenticate() 的核心作用）
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginUser,  // 认证后的用户信息
                null,       // 密码凭证（无需存储）
                loginUser.getAuthorities()  // 用户权限
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 4. 生成 Token 并存入 Redis（保留原有逻辑不变）
        int userId = user.getId();
        String token = null;
        try {
            token = JjwtUtil.getLoginToken(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        map.put("token", token);
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY + userId, token);

        return map;
    }
}