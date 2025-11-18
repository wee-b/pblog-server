package com.pblog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.rabc.PbUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<PbUserRole> {

    /**
     * 涉及到多表
     */
    // 自定义方法：根据用户ID查询角色标识（role_key）
    @Select("SELECT r.role_key FROM pb_role r " +
            "JOIN pb_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRoleKeysByUserId(Integer userId);


}
