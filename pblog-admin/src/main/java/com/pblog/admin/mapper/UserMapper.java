package com.pblog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select u.* from pb_user u where u.username = #{username} and u.del_flag = '0'")
    User selectUserByUsername(@Param("username") String username);
}
