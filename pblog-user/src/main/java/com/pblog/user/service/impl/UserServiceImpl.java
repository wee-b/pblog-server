package com.pblog.user.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pblog.common.ExceptionHandler.BusinessException;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.dto.*;
import com.pblog.common.entity.User;
import com.pblog.common.utils.RandomCodeUtil;
import com.pblog.user.mapper.UserMapper;
import com.pblog.user.service.CodeService;
import com.pblog.user.service.UserService;
import com.pblog.common.utils.JjwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    //
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, String> passwordLogin(PasswordLoginDTO passwordLoginDTO) throws BadCredentialsException {
        Map<String, String> map = new HashMap<>();

        // 1. 手动查询用户信息（复用 UserDetailsService 的逻辑，避免重复代码）
        UserDetails userDetails = userDetailsService.loadUserByUsername(passwordLoginDTO.getUsername());
        LoginUser loginUser = (LoginUser) userDetails;
        User user = loginUser.getUser();

        // 2. 手动校验密码（与 SecurityConfig 中配置的 PasswordEncoder 一致）
        if (!passwordEncoder.matches(passwordLoginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }


        // 3. 手动创建认证对象，存入 （替代 authenticate() 的核心作用）
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

    /**
     * 邮箱密码登录
     * @param emailLoginDTO
     * @return
     */
    public Map<String, String> emailLogin(EmailLoginDTO emailLoginDTO) {
        Map<String, String> map = new HashMap<>();

        // 1.检查邮箱用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailLoginDTO.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("用户不存在，请检查邮箱");
        }

        // 2. 手动校验密码（与 SecurityConfig 中配置的 PasswordEncoder 一致）
        if (!passwordEncoder.matches(emailLoginDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }


        // 3. 手动创建认证对象，存入SecurityContextHolder （替代 authenticate() 的核心作用）
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        // TODO 查询权限信息
        loginUser.setAuthorities(null);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginUser,  // 认证后的用户信息
                null,       // 密码凭证（无需存储）
                loginUser.getAuthorities()  // 用户权限
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 4. 生成 Token 并存入 Redis
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

    /**
     * 邮箱验证码登录
     * @param emailCodeLoginDTO
     * @return
     */
    @Override
    public Map<String, String> emailCodeLogin(EmailCodeLoginDTO emailCodeLoginDTO) {
        // 1.检查邮箱用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailCodeLoginDTO.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("用户不存在，请检查邮箱");
        }
        // 2.校验验证码
        String key = RedisConstants.LOGIN_EmailCode_KEY + emailCodeLoginDTO.getEmail();
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) {
            throw new BusinessException("验证码已过期，请重新发送");
        } else if (!code.equals(emailCodeLoginDTO.getCode())) {
            throw new BusinessException("验证码错误");
        }
        // 3.手动创建认证对象，存入SecurityContextHolder
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        // TODO 查询权限信息
        loginUser.setAuthorities(null);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginUser,  // 认证后的用户信息
                null,       // 密码凭证（无需存储）
                loginUser.getAuthorities()  // 用户权限
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 4. 生成 Token 并存入 Redis
        int userId = user.getId();
        String token = null;
        try {
            token = JjwtUtil.getLoginToken(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY + userId, token);

        return map;
    }

    @Override
    public String register(RegisterDTO registerDTO) {

        // 1.检查邮箱是否被注册
        if (registerDTO.getEmail() != null) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", registerDTO.getEmail());
            User user = userMapper.selectOne(queryWrapper);
            if (user == null) {
                throw new BusinessException("用户不存在，请检查邮箱");
            }

            // 2.校验验证码
            String key = RedisConstants.LOGIN_TOKEN_KEY + registerDTO.getEmail();
            String code = stringRedisTemplate.opsForValue().get(key);
            if (code == null) {
                throw new BusinessException("验证码已过期，请重新发送");
            } else if (!code.equals(registerDTO.getCode())) {
                throw new BusinessException("验证码错误");
            }
        }

        // 最多重试3次，避免无限循环
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                String username = RandomCodeUtil.generate10DigitNumber();
                String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
                User user = new User();
                user.setUsername(username);
                user.setPassword(encodedPassword);
                user.setEmail(registerDTO.getEmail());
                int rows = userMapper.insert(user);

                if (rows > 0) {
                    return username;
                }
            } catch (DataIntegrityViolationException e) {
                // 捕获唯一约束冲突异常（用户名重复）
                retryCount++;
                if (retryCount >= 3) {
                    throw new BusinessException("注册失败，请稍后重试");
                }
            }
        }
        throw new BusinessException("注册失败，请稍后重试");
    }


}