package com.pblog.admin.service;

import com.pblog.common.vo.RoleVO;

import java.util.List;

public interface RoleService {
    List<RoleVO> getAllRoles();

    RoleVO getRoleById(Integer id);

    void addRole(RoleVO role);

    void updateRole(RoleVO role);

    void deleteRoleById(Integer id);

    void enableRole(Integer id);
}
