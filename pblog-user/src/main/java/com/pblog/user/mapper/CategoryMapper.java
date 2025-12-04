package com.pblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.Category;
import com.pblog.common.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("""
        SELECT 
            c.id,
            c.category_name AS categoryName,
            c.parent_id AS parentId,
            c.order_num AS orderNum,
            c.description,
            COUNT(ac.article_id) AS articleCount
        FROM 
            pb_category c
        LEFT JOIN 
            pb_ac_relation ac ON c.id = ac.category_id
        -- 关键：添加 GROUP BY，包含所有非聚合字段
        GROUP BY 
            c.id, c.category_name, c.parent_id, c.order_num, c.description
        -- 可选：排序（按父分类→显示顺序）
        ORDER BY 
            c.parent_id ASC, c.order_num ASC
    """)
    List<CategoryVO> selectCategoryVO();


}
