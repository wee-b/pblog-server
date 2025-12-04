package com.pblog.common.filter;

import com.pblog.common.constant.RedisConstants;
import com.pblog.common.dto.LoginUser;
import com.pblog.common.utils.JjwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    // 白名单路径常量列表，集中管理无需验证的路径
    // TODO 之后可以实现一下白名单配置热更新
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/error",
            "/hello",
            "/user/passwordLogin",
            "/user/emailLoginOrRegister",
            "/user//getUserInfoByUserName",
            "/code/email/sendEmail",
            "/code/picture/generate",
            "/admin/login",
            "/admin/addPerson"      // TODO "/admin/addPerson" 后续移除白名单，只有超管才能注册管理员
            "/article/queryById/**",
    );

    private final StringRedisTemplate stringRedisTemplate;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationTokenFilter(StringRedisTemplate stringRedisTemplate, UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.stringRedisTemplate = stringRedisTemplate;
        log.info("JwtAuthenticationTokenFilter 已被 Spring 实例化");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        log.info("=== JWT过滤器收到请求：URI=" + requestUri);

        // 检查是否为白名单路径
        if (WHITE_LIST.contains(requestUri)) {
            log.info("=== 白名单路径，直接放行：" + requestUri);
            filterChain.doFilter(request, response);
            return;
        }


        log.info("=== 非白名单路径，执行Token验证：" + requestUri);

        // 1. 获取token
        String token = request.getHeader("token");

        // 2. 如果token为空，直接放行（让后续过滤器处理匿名访问）
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 验证token并获取用户ID
        int userId = JjwtUtil.verifyLoginToken(token);

        // 4. 处理验证结果
        if (userId == -1) {
            log.warn("无效的token: {}", token);
            filterChain.doFilter(request, response);
            return;
        }

        if (userId == 0) {
            log.warn("token已过期: {}", token);
            filterChain.doFilter(request, response);
            return;
        }

        // 5. 从Redis中获取用户信息并验证
        String redisKey = RedisConstants.LOGIN_TOKEN_KEY + userId;
        String redisToken = stringRedisTemplate.opsForValue().get(redisKey);

        if (!StringUtils.hasText(redisToken) || !redisToken.equals(token)) {
            log.warn("token已过期或已被注销: userId={}", userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 6. 从数据库加载完整用户信息（包含权限）
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(String.valueOf(userId));
        if (Objects.isNull(loginUser)) {
            log.warn("用户不存在: userId={}", userId);
            filterChain.doFilter(request, response);
            return;
        }

        // 7. 将用户信息存入SecurityContextHolder
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);



        // 8. 刷新token有效期（滑动窗口策略）测试阶段注释代码
//        try {
//            String newToken = JjwtUtil.getLoginToken(userId);
//            stringRedisTemplate.opsForValue().set(redisKey, newToken);
//            response.setHeader("token", newToken);
//            log.info("用户token已刷新: userId={}", userId);
//        } catch (Exception e) {
//            log.error("刷新token失败: userId={}", userId, e);
//        }

        // 9. 放行
        filterChain.doFilter(request, response);
    }
}