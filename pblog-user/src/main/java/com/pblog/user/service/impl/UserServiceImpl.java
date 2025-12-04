package com.pblog.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pblog.common.Expection.BusinessException;
import com.pblog.common.constant.DefaultConstants;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.constant.RoleConstant;
import com.pblog.common.dto.*;
import com.pblog.common.dto.login.EmailCodeDTO;
import com.pblog.common.dto.login.PasswordLoginDTO;
import com.pblog.common.entity.User;
import com.pblog.common.entity.rabc.PbUserRole;
import com.pblog.common.utils.RandomCodeUtil;
import com.pblog.common.utils.SecurityContextUtil;
import com.pblog.common.vo.UserInfoVO;
import com.pblog.user.mapper.UserMapper;
import com.pblog.user.mapper.UserRoleMapper;
import com.pblog.user.service.CodeService;
import com.pblog.user.service.FileService;
import com.pblog.user.service.UserService;
import com.pblog.common.utils.JjwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private CodeService codeService;

    @Resource
    private FileService fileService;

    /**
     * 账号密码登录
     * @param passwordLoginDTO
     * @return
     * @throws BadCredentialsException
     */
    @Override
    public Map<String, String> passwordLogin(PasswordLoginDTO passwordLoginDTO) throws BadCredentialsException {

        // 0.校验图片验证码（核心逻辑，复用 codeService）
        boolean captchaValid = codeService.verifyCaptcha(
                passwordLoginDTO.getCaptchaUuid(),
                passwordLoginDTO.getCaptchaCode()
        );
        if (!captchaValid) {
            throw new BusinessException("图片验证码错误或已过期");
        }


        // 1. 手动查询用户信息
        LoginUser loginUser = QueryLoginUserByOneColumn("username", passwordLoginDTO.getUsername());

        // 2. 手动校验密码（与 SecurityConfig 中配置的 PasswordEncoder 一致）
        if (!passwordEncoder.matches(passwordLoginDTO.getPassword(), loginUser.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }


        // 3. 手动创建认证对象，存入 （替代 authenticate() 的核心作用）
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginUser,  // 认证后的用户信息
                null,       // 密码凭证（无需存储）
                loginUser.getAuthorities()  // 用户权限
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 4. 生成 Token 并存入 Redis
        int userId = loginUser.getUser().getId();
        String token = null;
        try {
            token = JjwtUtil.getLoginToken(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 5. 组装信息并返回
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(loginUser.getUser(),userInfoVO);
        String userInfoJson = JSON.toJSONString(userInfoVO);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("userInfoJson",userInfoJson);
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY + userId, token);

        return map;
    }

    /**
     * 邮箱验证码登录或者注册
     */
    @Override
    public Map<String, String> emailLoginOrRegister(EmailCodeDTO emailCodeDTO) {

        // 0.校验图片验证码（核心逻辑，复用 codeService）
        boolean captchaValid = codeService.verifyCaptcha(
                emailCodeDTO.getCaptchaUuid(),
                emailCodeDTO.getCaptchaCode()
        );
        if (!captchaValid) {
            throw new BusinessException("验证码错误或已过期"); // 自定义业务异常
        }

        // 1.检查用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailCodeDTO.getEmail());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            // 用户不存在自动注册
            user = emailRegister(emailCodeDTO.getEmail());
        }
        // 查询权限信息
        List<String> lis = userRoleMapper.selectRoleKeysByUserId(user.getId());
        LoginUser loginUser =  new LoginUser(user,lis);


        // 2.校验邮箱验证码
        codeService.verifyEmailCode(
                RedisConstants.LOGIN_EmailCode_KEY + emailCodeDTO.getEmail(),
                emailCodeDTO.getCode()
        );

        // 3.手动创建认证对象，存入SecurityContextHolder
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginUser,  // 认证后的用户信息
                null,       // 密码凭证（无需存储）
                loginUser.getAuthorities()  // 用户权限
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 4. 生成 Token 并存入 Redis
        int userId = loginUser.getUser().getId();
        String token = null;
        try {
            token = JjwtUtil.getLoginToken(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 5. 组装信息并返回
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(loginUser.getUser(),userInfoVO);
        String userInfoJson = JSON.toJSONString(userInfoVO);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("userInfoJson",userInfoJson);
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY + userId, token);

        return map;
    }

    private @NotNull User emailRegister(String email ) {

        User user = new User();
        // 最多重试3次，避免无限循环
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                String username = RandomCodeUtil.generate10DigitNumber();
                String encodedPassword = passwordEncoder.encode(DefaultConstants.Default_password);
                user.setUsername(username);
                user.setPassword(encodedPassword);
                user.setEmail(email);
                user.setAvatarUrl(DefaultConstants.DEFAULT_AVATAR_FILENAME);
                user.setStatus(DefaultConstants.DEFAULT_STATUS);
                user.setDelFlag(DefaultConstants.DEFAULT_DELFLAG);
                int rows = userMapper.insert(user);

                // 为用户分配权限
                Integer userId = user.getId();
                Integer roleId = user.getEmail() == null ? RoleConstant.VISITOR_ROLE_ID: RoleConstant.NORMAL_USER_ROLE_ID;
                PbUserRole pbUserRole = new PbUserRole();
                pbUserRole.setRoleId(roleId);
                pbUserRole.setUserId(userId);

                userRoleMapper.insert(pbUserRole);

                if (rows > 0) {
                    return user;
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

    // 邮箱+密码登录接口，接口弃用
    public Map<String, String> emailLogin(EmailLoginDTO emailLoginDTO) {

        // 0.校验图片验证码（核心逻辑，复用 codeService）
        boolean captchaValid = codeService.verifyCaptcha(
                emailLoginDTO.getCaptchaUuid(),
                emailLoginDTO.getCaptchaCode()
        );
        if (!captchaValid) {
            throw new BusinessException("验证码错误或已过期"); // 自定义业务异常
        }

        Map<String, String> map = new HashMap<>();

        // 1.检查邮箱用户是否存在
        LoginUser loginUser = QueryLoginUserByOneColumn("email", emailLoginDTO.getEmail());

        // 2. 手动校验密码（与 SecurityConfig 中配置的 PasswordEncoder 一致）
        if (!passwordEncoder.matches(emailLoginDTO.getPassword(), loginUser.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }


        // 3. 手动创建认证对象，存入SecurityContextHolder （替代 authenticate() 的核心作用）
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginUser,  // 认证后的用户信息
                null,       // 密码凭证（无需存储）
                loginUser.getAuthorities()  // 用户权限
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 4. 生成 Token 并存入 Redis
        int userId = loginUser.getUser().getId();
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

    // 普通注册接口，接口弃用
    @Override
    public String register(RegisterDTO registerDTO) {

        // 0.校验图片验证码（核心逻辑，复用 codeService）
        boolean captchaValid = codeService.verifyCaptcha(
                registerDTO.getCaptchaUuid(),
                registerDTO.getCaptchaCode()
        );
        if (!captchaValid) {
            throw new BusinessException("验证码错误或已过期"); // 自定义业务异常
        }

        // 1.检查邮箱是否被注册
        if (registerDTO.getEmail() != null) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", registerDTO.getEmail());
            User user = userMapper.selectOne(queryWrapper);
            if (user != null) {
                throw new BusinessException("该邮箱已绑定用户，请更换邮箱");
            }

            // 2.校验邮箱验证码
            codeService.verifyEmailCode(
                    RedisConstants.LOGIN_EmailCode_KEY + registerDTO.getEmail(),
                    registerDTO.getCode()
            );
        }

        User user = emailRegister(registerDTO.getEmail());
        return user.getUsername();
    }


    // 未登录重置密码，接口弃用
    @Override
    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 1.检查用户是否存在
        QueryLoginUserByOneColumn("email", resetPasswordDTO.getEmail());
        // 2.校验验证码
        codeService.verifyEmailCode(
                RedisConstants.LOGIN_EmailCode_KEY + resetPasswordDTO.getEmail(),
                resetPasswordDTO.getCode()
        );

        // 3.更新
        String encodedPassword = passwordEncoder.encode(resetPasswordDTO.getNewPassword());
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getEmail, resetPasswordDTO.getEmail())  // 条件
                .set(User::getPassword, encodedPassword);     // 更新

        userMapper.update(null, lambdaUpdateWrapper);
        return "密码重置成功";
    }



    // 需要登录
    @Override
    public String updateInfo(UserDTO userDTO) {

        String username = SecurityContextUtil.getUsername();

        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUsername, username)  // 条件
                .set(User::getEmail, userDTO.getNickname())
                .set(User::getEmail, userDTO.getAvatarUrl())
                .set(User::getEmail, userDTO.getBio());

        // TODO 同步更新文章里面作者的nickname

        int updated = userMapper.update(null, lambdaUpdateWrapper);
        if (updated == 0) {
            throw new RuntimeException("用户信息更新失败");
        }

        return username;
    }

    @Override
    public String updateEmail(EmailCodeDTO emailCodeDTO) {

        // 0.校验图片验证码（核心逻辑，复用 codeService）
        boolean captchaValid = codeService.verifyCaptcha(
                emailCodeDTO.getCaptchaUuid(),
                emailCodeDTO.getCaptchaCode()
        );
        if (!captchaValid) {
            throw new BusinessException("验证码错误或已过期"); // 自定义业务异常
        }
        // 1.检验是否为原来的邮箱
        User user = SecurityContextUtil.getUser();
        String oldEmail = SecurityContextUtil.getUser().getEmail();
        if (oldEmail != null && emailCodeDTO.getEmail() == oldEmail){
            throw new BusinessException("不可以设置为原来的有邮箱");
        }

        // 2.校验验证码
        codeService.verifyEmailCode(
                RedisConstants.LOGIN_EmailCode_KEY + emailCodeDTO.getEmail(),
                emailCodeDTO.getCode()
        );

        // 3.第一次绑定邮箱后,变更用户权限:游客-->正式用户
        if (oldEmail == null){
            LambdaUpdateWrapper<PbUserRole> luw = new LambdaUpdateWrapper<>();
            luw.eq(PbUserRole::getUserId,user.getId())
                    .set(PbUserRole::getRoleId, RoleConstant.NORMAL_USER_ROLE_ID);
            userRoleMapper.update(null, luw);
        }

        // 4.更新
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUsername, user.getUsername())  // 条件
                .set(User::getEmail, emailCodeDTO.getEmail());     // 更新

        userMapper.update(null, lambdaUpdateWrapper);
        return user.getUsername();
    }

    @Override
    public String forgetPassword(String newPassword) {
        // 获取当前登录用户
        String username = SecurityContextUtil.getUsername();

        String encodedPassword = passwordEncoder.encode(newPassword);
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUsername, username)  // 条件
                .set(User::getPassword, encodedPassword);     // 更新

        userMapper.update(null, lambdaUpdateWrapper);
        return username;
    }

    @Override
    public String logout() {

        Integer id = SecurityContextUtil.getUser().getId();

        Boolean deleted = stringRedisTemplate.delete(RedisConstants.LOGIN_TOKEN_KEY + id);
        if (!deleted) {
            throw new BusinessException("退出失败");
        }

        return "退出成功";
    }

    /**
     * 用户注销接口
     * @param request
     * @param response
     * @return
     */
    @Override
    public String deleteAccount(HttpServletRequest request, HttpServletResponse response) {

        // 1. 获取当前登录用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BusinessException("未登录，无法注销");
        }
        // 2. 执行退出登录（清除认证信息和Session）
        // 使用 Spring Security 内置的 logout 处理器
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication); // 触发退出逻辑

        // 3. 执行账号注销（删除用户数据）
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String username = loginUser.getUsername();
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("username", username);
//        int rows = userMapper.delete(queryWrapper);
        // TODO 改为逻辑删除，后续可采用延迟队列清除
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUsername, username)  // 条件
                .set(User::getDelFlag, DefaultConstants.Deleted_DelFlag);     // 更新

        userMapper.update(null, lambdaUpdateWrapper);
        log.info("{}用户注销成功",username);

        return "账号注销成功";
    }

    @Override
    public UserInfoVO getUserInfo() {

        User user = SecurityContextUtil.getUser();
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    @Override
    public UserInfoVO getUserInfoByUserName(String username) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }


    // =======================================  private函数  =======================================


    /**
     * 查询结果用户应该存在,作用与loadUserByUsername方法相同
     * @param column
     * @param value
     * @return
     */
    private LoginUser QueryLoginUserByOneColumn(String column,String value) throws BusinessException {

        String message = "";
        if(column.equals("username")){
            message = "用户名";
        }else if(column.equals("email")){
            message = "邮箱";
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column, value);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null || user.getDelFlag().equals("1")) {
            throw new BusinessException("用户不存在，请检查"+message);
        }

        if(user.getStatus().equals("0")){
            throw new DisabledException("账号已被禁用");
        }

        // 查询权限信息
        List<String> lis = userRoleMapper.selectRoleKeysByUserId(user.getId());
        return new LoginUser(user,lis);
    }


}