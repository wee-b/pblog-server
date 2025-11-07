package com.pblog.common.config;

import com.pblog.common.filter.JwtAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /**
     * 配置安全过滤链（核心配置）
     * 替代旧版本的 configure(HttpSecurity) 方法，通过 SecurityFilterChain 定义安全规则
     * @param http HttpSecurity 配置对象
     * @return 安全过滤链实例
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        // 禁用 CSRF 防护（前后端分离场景常用）
        .csrf(csrf -> csrf.disable())
        .formLogin(form -> form.disable()) // 禁用默认表单登录过滤器


        // 配置 Session 管理
        .sessionManagement(session ->
                // 设置 Session 为无状态，不存储用户会话信息
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )


        // 配置请求授权规则
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll() // 所有路径允许匿名访问
                // 登录接口允许匿名访问
//                .requestMatchers("/user/passwordLogin").anonymous()
//                .requestMatchers("/hello").permitAll()
                // 其他所有请求必须认证
//                .anyRequest().authenticated()
        );

        // 将jwtAuthenticationTokenFilter过滤器加在UsernamePasswordAuthenticationFilter之前
        http.addFilterBefore(new JwtAuthenticationTokenFilter(redisTemplate,userDetailsService), UsernamePasswordAuthenticationFilter.class);

        // 配置异常处理器
        http.exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)  // 认证失败处理
            .accessDeniedHandler(accessDeniedHandler)          // 权限不足处理
        );

        // 允许跨域请求
        http.cors(cors -> cors
            .configurationSource(corsConfigurationSource())
        );

        // 构建并返回安全过滤链
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许的前端域名（生产环境需指定具体域名，不要用*）
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080","http://localhost:8081", "http://127.0.0.1:5500"));
        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 允许携带凭证（如cookie）
        configuration.setAllowCredentials(true);
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用跨域配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    // 如果需要自定义用户认证（替代原来的configure(AuthenticationManagerBuilder)方法）
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }

    // 密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //将 Spring Security 内部的 AuthenticationManager 暴露为一个 Spring 容器中的 Bean，
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
