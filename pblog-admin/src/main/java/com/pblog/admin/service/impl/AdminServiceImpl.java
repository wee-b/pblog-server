package com.pblog.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pblog.admin.mapper.UserMapper;
import com.pblog.admin.mapper.UserRoleMapper;
import com.pblog.admin.service.AdminService;
import com.pblog.admin.service.CodeService;
import com.pblog.common.Expection.BusinessException;
import com.pblog.common.constant.DefaultConstants;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.constant.RoleConstant;
import com.pblog.common.dto.LoginUser;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.dto.RegisterDTO;
import com.pblog.common.dto.login.PasswordLoginDTO;
import com.pblog.common.entity.User;
import com.pblog.common.entity.rabc.PbUserRole;
import com.pblog.common.utils.JjwtUtil;
import com.pblog.common.utils.RandomCodeUtil;
import com.pblog.common.utils.SecurityContextUtil;
import com.pblog.common.vo.UserAdminInfoVO;
import com.pblog.common.vo.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CodeService codeService;


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


    @Override
    public Map<String, String> register(RegisterDTO registerDTO) {

        // 校验图片验证码（核心逻辑，复用 codeService）
        boolean captchaValid = codeService.verifyCaptcha(
                registerDTO.getCaptchaUuid(),
                registerDTO.getCaptchaCode()
        );
        if (!captchaValid) {
            throw new BusinessException("验证码错误或已过期"); // 自定义业务异常
        }


        // 最多重试3次，避免无限循环
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                String username = RandomCodeUtil.generate10DigitNumber();
                String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
                User user = new User();
                // todo
                user.setNickname("新用户");
                user.setUsername(username);
                user.setPassword(encodedPassword);
                user.setEmail(null);
                user.setAvatarUrl(DefaultConstants.DEFAULT_AVATAR_FILENAME);
                user.setStatus(DefaultConstants.DEFAULT_STATUS);
                user.setDelFlag(DefaultConstants.DEFAULT_DELFLAG);
                int rows = userMapper.insert(user);

                // 为用户分配权限
                Integer userId = user.getId();
                Integer roleId = RoleConstant.AUDITOR_ROLE_ID;
                PbUserRole pbUserRole = new PbUserRole();
                pbUserRole.setRoleId(roleId);
                pbUserRole.setUserId(userId);

                userRoleMapper.insert(pbUserRole);

                if (rows > 0) {
                    // 4. 生成 Token 并存入 Redis
                    String token = null;
                    try {
                        token = JjwtUtil.getLoginToken(userId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // 5. 组装信息并返回
                    UserInfoVO userInfoVO = new UserInfoVO();
                    BeanUtils.copyProperties(user,userInfoVO);
                    String userInfoJson = JSON.toJSONString(userInfoVO);
                    Map<String, String> map = new HashMap<>();
                    map.put("token", token);
                    map.put("userInfoJson",userInfoJson);
                    stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_TOKEN_KEY + userId, token);

                    return map;
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


    @Override
    public void logout() {
        Integer id = SecurityContextUtil.getUser().getId();

        Boolean deleted = stringRedisTemplate.delete(RedisConstants.LOGIN_TOKEN_KEY + id);
        if (!deleted) {
            throw new BusinessException("退出失败");
        }

    }

    @Override
    public void edit(String username) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 处理用户不存在的情况
        if (user == null) {
            throw new BusinessException("用户不存在：" + username); // 或记录日志+返回
        }

        String status = user.getStatus().equals("1")?"0":"1";

        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUsername, username)  // 条件
                .set(User::getStatus, status);
        int rows = userMapper.update(null, lambdaUpdateWrapper);

        // 6. 验证更新结果（可选，根据业务严格程度）
        if (rows == 0) {
            throw new BusinessException("更新状态失败，用户可能已被删除");
        }
    }

    @Override
    public List<UserAdminInfoVO> UserPageQuery(PageQueryDTO pageQueryDTO) {

        // 1. 创建分页对象（Page 构造器：页码、每页条数）
        Page<User> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        // 2. 调用 selectPage 方法（第二个参数为查询条件，可为 null 表示查询全部）
        IPage<User> userPage = userMapper.selectPage(page, null);

        List<UserAdminInfoVO> collect1 = userPage.getRecords().stream()
                .map(user -> {
                    UserAdminInfoVO vo = new UserAdminInfoVO();
                    // 复制同名字段（源对象，目标对象）
                    BeanUtils.copyProperties(user, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        return collect1;
    }

    @Override
    public User getUserAllInfo(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }



//    @Override
//    public void updatePerson(AdminRegisterDTO adminRegisterDTO) {
//
//        String username = SecurityContextUtil.getUsername();
//        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.eq("username", username);
//        User user = userMapper.selectOne(queryWrapper);
//
//        // 处理用户不存在的情况
//        if (user == null) {
//            throw new BusinessException("用户不存在：" + username); // 或记录日志+返回
//        }
//
//        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        lambdaUpdateWrapper.eq(User::getUsername, username)  // 条件
//                .set(User::getPassword, passwordEncoder.encode(adminRegisterDTO.getPassword()));
//        int rows = userMapper.update(null, lambdaUpdateWrapper);
//
//        // 6. 验证更新结果（可选，根据业务严格程度）
//        if (rows == 0) {
//            throw new BusinessException("更新状态失败，用户可能已被删除");
//        }
//    }


    /**
     * 查询结果用户应该存在,作用与loadUserByUsername方法相同
     * @param column
     * @param value
     * @return
     */
    private LoginUser QueryLoginUserByOneColumn(String column, String value) throws BusinessException {

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

        // 检验账号状态
        if(user.getStatus().equals("1")){
            throw new DisabledException("账号已被禁用");
        }

        // 查询权限信息
        List<String> lis = userRoleMapper.selectRoleKeysByUserId(user.getId());
        return new LoginUser(user,lis);
    }
}
