package com.pblog.admin.mapper;

import com.pblog.common.entity.rabc.PbMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<PbMenu> {

    @Select("SELECT DISTINCT m.perms FROM pb_menu m " +
            "JOIN pb_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id IN #{roleIds} AND m.perms IS NOT NULL")
    List<String> selectPermsByRoleIds(@Param("roleIds") List<Integer> roleIds);
}
