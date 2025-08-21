package com.pblog.service;

import com.pblog.dto.CategoryDto;
import com.pblog.entity.Category;
import com.pblog.result.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
     * @param pageNo 筛选条件
     * @param pageSize      分页对象
     * @return 查询结果
     */
    PageResult queryByPage(Integer pageNo, Integer pageSize);

    /**
     * 新增数据
     *
     * @param categoryDto 实例对象
     * @return 实例对象
     */
    void add(CategoryDto categoryDto);

    /**
     * 修改数据
     *
     * @param categoryDto 实例对象
     * @return 实例对象
     */
    void edit(CategoryDto categoryDto);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    void deleteById(Integer id);

    List<Category> getAll();

}
