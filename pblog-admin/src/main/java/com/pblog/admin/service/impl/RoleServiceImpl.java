package com.pblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pblog.admin.mapper.RoleMapper;
import com.pblog.admin.mapper.RoleMenuMapper;
import com.pblog.admin.service.RoleService;
import com.pblog.common.constant.DefaultConstants;
import com.pblog.common.entity.rabc.PbRole;
import com.pblog.common.entity.rabc.PbRoleMenu;
import com.pblog.common.vo.RoleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;


    @Override
    public List<RoleVO> getAllRoles() {

        List<PbRole> roles = roleMapper.selectList(null);

        List<RoleVO> collect1 = roles.stream()
            .map(role -> {
                RoleVO vo = new RoleVO();
                // 只填充部分字段
                vo.setId(role.getId());
                vo.setRoleName(role.getRoleName());
                vo.setRoleSort(role.getRoleSort());
                return vo;
            })
            .collect(Collectors.toList());


        return collect1;
    }

    @Override
    public RoleVO getRoleById(Integer id) {
        Assert.notNull(id, "角色ID不能为空");

        PbRole role = roleMapper.selectById(id);
        Assert.notNull(role, "角色不存在");
        Assert.isTrue(DefaultConstants.DEFAULT_DELFLAG.equals(role.getDelFlag()), "角色已删除");

        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);

        // 查询关联的菜单ID
        List<Integer> menuIds = roleMenuMapper.selectList(
                        new LambdaQueryWrapper<PbRoleMenu>()
                                .eq(PbRoleMenu::getRoleId, id)
                ).stream()
                .map(PbRoleMenu::getMenuId)
                .collect(Collectors.toList());
        vo.setMenuIds(menuIds);

        return vo;
    }

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addRole(RoleVO role) {
        Assert.notNull(role, "角色信息不能为空");
        Assert.hasText(role.getRoleName(), "角色名称不能为空");
        Assert.hasText(role.getRoleKey(), "角色权限标识不能为空");
        Assert.notNull(role.getRoleSort(), "显示顺序不能为空");

        // 1. 新增角色表记录
        PbRole pbRole = new PbRole();
        BeanUtils.copyProperties(role, pbRole);
        pbRole.setStatus(DefaultConstants.DEFAULT_STATUS); // 默认启用
        pbRole.setDelFlag(DefaultConstants.DEFAULT_DELFLAG); // 默认未删除
        roleMapper.insert(pbRole);

        // 2. 新增角色-菜单关联关系
        saveRoleMenus(pbRole.getId(), role.getMenuIds());
    }

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateRole(RoleVO role) {
        Assert.notNull(role, "角色信息不能为空");
        Assert.notNull(role.getId(), "角色ID不能为空");

        // 校验角色存在性
        PbRole existingRole = roleMapper.selectById(role.getId());
        Assert.notNull(existingRole, "角色不存在");
        Assert.isTrue(DefaultConstants.DEFAULT_DELFLAG.equals(existingRole.getDelFlag()), "角色已删除");

        // 1. 更新角色表
        PbRole updateRole = new PbRole();
        BeanUtils.copyProperties(role, updateRole);
        // 保留创建信息不被覆盖
        updateRole.setCreateBy(existingRole.getCreateBy());
        updateRole.setCreateTime(existingRole.getCreateTime());
        updateRole.setDelFlag(existingRole.getDelFlag()); // 不允许修改删除状态
        roleMapper.updateById(updateRole);

        // 2. 先删除原有角色-菜单关联，再重新添加
        roleMenuMapper.delete(
                new LambdaQueryWrapper<PbRoleMenu>()
                        .eq(PbRoleMenu::getRoleId, role.getId())
        );
        saveRoleMenus(role.getId(), role.getMenuIds());
    }

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRoleById(Integer id) {
        Assert.notNull(id, "角色ID不能为空");

        // 校验角色是否存在
        PbRole role = roleMapper.selectById(id);
        Assert.notNull(role, "角色不存在");
        Assert.isTrue(DefaultConstants.DEFAULT_DELFLAG.equals(role.getDelFlag()), "角色已删除");

        roleMapper.deleteById(id);

        // 2. 删除关联的角色-菜单关系
        roleMenuMapper.delete(
                new LambdaQueryWrapper<PbRoleMenu>()
                        .eq(PbRoleMenu::getRoleId, id)
        );
    }

    @Override
    public void enableRole(Integer id) {
        Assert.notNull(id, "角色ID不能为空");

        PbRole role = roleMapper.selectById(id);
        Assert.notNull(role, "角色不存在");
        Assert.isTrue(DefaultConstants.DEFAULT_DELFLAG.equals(role.getDelFlag()), "角色已删除");

        // 切换状态（1正常 <-> 0停用）
        PbRole updateRole = new PbRole();
        updateRole.setId(id);
        updateRole.setStatus("1".equals(role.getStatus()) ? "0" : "1");
        roleMapper.updateById(updateRole);
    }

    /**
     * 保存角色与菜单的关联关系
     */
    private void saveRoleMenus(Integer roleId, List<Integer> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            return; // 没有选择菜单则不关联
        }

        // 批量添加角色-菜单关联
        List<PbRoleMenu> roleMenus = menuIds.stream()
                .map(menuId -> {
                    PbRoleMenu rm = new PbRoleMenu();
                    rm.setRoleId(roleId);
                    rm.setMenuId(menuId);
                    return rm;
                })
                .collect(Collectors.toList());

        roleMenuMapper.batchInsert(roleMenus); // 需要在RoleMenuMapper中定义批量插入方法
    }
}
