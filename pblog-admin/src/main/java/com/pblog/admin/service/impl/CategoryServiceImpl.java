package com.pblog.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pblog.admin.service.CategoryService;
import com.pblog.common.Expection.BusinessException;
import com.pblog.common.dto.admin.CategoryDTO;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.entity.Category;
import com.pblog.admin.mapper.CategoryMapper;
import com.pblog.common.result.PageResult;
import com.pblog.common.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


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
    public PageResult queryByPage(PageQueryDTO pageQueryDTO) {
        // 1. 创建分页对象（Page 构造器：页码、每页条数）
        Page<Category> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        // 2. 调用 selectPage 方法（第二个参数为查询条件，可为 null 表示查询全部）
        IPage<Category> categoryPage = categoryMapper.selectPage(page, null);

        List<CategoryVO> collect = categoryPage.getRecords().stream().map(
                category -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                }
        ).collect(Collectors.toList());


        return new PageResult(collect.size(), collect);
    }

    @Override
    public void add(CategoryDTO categoryDto) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        int inserted = categoryMapper.insert(category);
        if (inserted == 0) {
            throw new BusinessException("新增失败");
        }
    }

    @Override
    public void edit(Integer id ,CategoryDTO categoryDto) {
        // 1. 校验ID是否存在（避免更新不存在的数据）
        if (id == null) {
            throw new IllegalArgumentException("ID不能为空");
        }
        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("该分类不存在，无法更新");
        }

        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteById(Integer id) {
        // 1. 校验ID是否存在（避免更新不存在的数据）
        if (id == null) {
            throw new IllegalArgumentException("ID不能为空");
        }
        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("该分类不存在，无法删除");
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public List<CategoryVO> getAll() {

        List<Category> list = categoryMapper.selectList(null);

        List<CategoryVO> collect = list.stream().map(
                category -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                }
        ).collect(Collectors.toList());

        return collect;
    }
}
