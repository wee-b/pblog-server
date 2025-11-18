package com.pblog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.rabc.PbRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<PbRoleMenu> {

    /**
     * 批量插入角色-菜单关联
     */
    void batchInsert(@Param("list") List<PbRoleMenu> roleMenus);
}