package com.pblog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.rabc.PbRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<PbRole> {
}
