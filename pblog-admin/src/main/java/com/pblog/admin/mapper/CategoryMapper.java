package com.pblog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
