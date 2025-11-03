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
import java.util.Objects;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final StringRedisTemplate stringRedisTemplate;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationTokenFilter(StringRedisTemplate stringRedisTemplate,UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.stringRedisTemplate = stringRedisTemplate;
        log.info("JwtAuthenticationTokenFilter 已被 Spring 实例化"); // 添加这行
    }




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 强制打印请求信息（不受日志框架影响）
        String requestUri = request.getRequestURI();
        log.info("=== JWT过滤器收到请求：URI=" + requestUri);

        // 白名单判断（简化，只判断/hello和/login）
        if ("/hello".equals(requestUri) || "/user/passwordLogin".equals(requestUri)) {
            log.info("=== 白名单路径，直接放行：" + requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        // 非白名单路径的Token验证逻辑...（保留原有代码）
        System.out.println("=== 非白名单路径，执行Token验证：" + requestUri);

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

        // 8. 刷新token有效期（滑动窗口策略）
        try {
            // 生成新的token（包含当前时间戳）
            String newToken = JjwtUtil.getLoginToken(userId);
            // 更新Redis中的token，设置新的过期时间
            stringRedisTemplate.opsForValue().set(redisKey, newToken);
            // 将新token通过响应头返回给前端
            response.setHeader("token", newToken);
            log.info("用户token已刷新: userId={}", userId);
        } catch (Exception e) {
            log.error("刷新token失败: userId={}", userId, e);
            // 刷新失败不影响当前请求处理，继续放行
        }

        // 9. 放行
        filterChain.doFilter(request, response);
    }
}
