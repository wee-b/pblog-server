package com.pblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pblog.admin.mapper.UserMapper;
import com.pblog.admin.mapper.UserRoleMapper;
import com.pblog.admin.service.AdminService;
import com.pblog.common.Expection.BusinessException;
import com.pblog.common.constant.DefaultConstants;
import com.pblog.common.constant.RedisConstants;
import com.pblog.common.constant.RoleConstant;
import com.pblog.common.dto.LoginUser;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.dto.PasswordLoginDTO;
import com.pblog.common.dto.admin.AdminRegisterDTO;
import com.pblog.common.entity.User;
import com.pblog.common.entity.rabc.PbUserRole;
import com.pblog.common.utils.JjwtUtil;
import com.pblog.common.utils.SecurityContextUtil;
import com.pblog.common.vo.UserAdminInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Map<String, String> login(PasswordLoginDTO passwordLoginDTO) {
        Map<String, String> map = new HashMap<>();

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

        // 4. 生成 Token 并存入 Redis（保留原有逻辑不变）
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

    // TODO 添加事务控制
    @Override
    public void addPerson(AdminRegisterDTO adminRegisterDTO) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", adminRegisterDTO.getUsername());
        User queryUser = userMapper.selectOne(queryWrapper);
        if (queryUser != null) {
            throw new BusinessException("用户已经存在");
        }

        User user = new User();
        user.setUsername(adminRegisterDTO.getUsername());
        user.setPassword(passwordEncoder.encode(adminRegisterDTO.getPassword()));
        user.setAvatarUrl(DefaultConstants.DEFAULT_AVATAR_FILENAME);
        user.setStatus(DefaultConstants.DEFAULT_STATUS);
        user.setDelFlag(DefaultConstants.DEFAULT_DELFLAG);

        int inserted = userMapper.insert(user);

        Integer userId = user.getId();
        PbUserRole pbUserRole = new PbUserRole();
        pbUserRole.setUserId(userId);
        pbUserRole.setRoleId(RoleConstant.AUDITOR_ROLE_ID);
        userRoleMapper.insert(pbUserRole);

        if (inserted == 0) {
            throw new BusinessException("用户添加失败");
        }
    }

    @Override
    public void updatePerson(AdminRegisterDTO adminRegisterDTO) {

        String username = SecurityContextUtil.getUsername();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 处理用户不存在的情况
        if (user == null) {
            throw new BusinessException("用户不存在：" + username); // 或记录日志+返回
        }

        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUsername, username)  // 条件
                .set(User::getPassword, passwordEncoder.encode(adminRegisterDTO.getPassword()));
        int rows = userMapper.update(null, lambdaUpdateWrapper);

        // 6. 验证更新结果（可选，根据业务严格程度）
        if (rows == 0) {
            throw new BusinessException("更新状态失败，用户可能已被删除");
        }
    }


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
        if(user.getStatus().equals("0")){
            throw new DisabledException("账号已被禁用");
        }

        // 查询权限信息
        List<String> lis = userRoleMapper.selectRoleKeysByUserId(user.getId());
        return new LoginUser(user,lis);
    }
}
