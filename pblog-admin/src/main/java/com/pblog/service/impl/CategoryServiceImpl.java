package com.pblog.service.impl;

import com.pblog.dto.CategoryDto;
import com.pblog.entity.Category;
import com.pblog.mapper.CategoryMapper;
import com.pblog.result.PageResult;
import com.pblog.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 博客分类表，存储文章分类信息，支持多级分类(Category)表服务实现类
 *
 * @author makejava
 * @since 2025-08-18 17:19:27
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Category queryById(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public PageResult queryByPage(Integer pageNo, Integer pageSize) {
        // TODO 分页查询待完成
        return null;
    }

    @Override
    public void add(CategoryDto categoryDto) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        categoryMapper.insert(category);
    }

    @Override
    public void edit(CategoryDto categoryDto) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteById(Integer id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public List<Category> getAll() {
        return categoryMapper.selectList(null);
    }
}
