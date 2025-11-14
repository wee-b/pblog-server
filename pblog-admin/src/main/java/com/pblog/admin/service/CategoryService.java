package com.pblog.admin.service;

import com.pblog.common.vo.CategoryVO;
import com.pblog.common.dto.admin.CategoryDTO;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.entity.Category;
import com.pblog.common.result.PageResult;

import java.util.List;

/**
 * 博客分类表，存储文章分类信息，支持多级分类(Category)表服务接口
 *
 * @author makejava
 * @since 2025-08-18 17:19:27
 */
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

    /**
     * 新增数据
     *
     * @param categoryDto 实例对象
     * @return 实例对象
     */
    void add(CategoryDTO categoryDto);

    /**
     * 修改数据
     *
     * @param categoryDto 实例对象
     * @return 实例对象
     */
    void edit(Integer id,CategoryDTO categoryDto);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    void deleteById(Integer id);

    List<CategoryVO> getAll();

}
