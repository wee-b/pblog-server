package com.pblog.user.service;

import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.entity.Category;
import com.pblog.common.result.PageResult;
import com.pblog.common.vo.CategoryVO;

import java.util.List;


public interface CategoryService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Category queryById(Integer id);

    /**
     * 分页查询
     *
     * @return 查询结果
     */
    PageResult queryByPage(PageQueryDTO pageQueryDTO);



    List<CategoryVO> getAll();

}
