package com.pblog.common.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.pblog.common.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private User user;

    private List<String> permissions;

    public LoginUser(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    //
    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;

    /**
     * 获取权限列表,重写这个方法后在创建
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (authorities != null) {
            return authorities;
        }

        // 把permissions 中的String类型的权限信息封装成SimpleGrantedAuthority对象
        authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getEmail(){
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 用户是否可用
    @Override
    public boolean isEnabled() {
        return true;
    }
}
