package com.pblog.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.entity.Category;
import com.pblog.common.result.PageResult;
import com.pblog.common.vo.CategoryVO;
import com.pblog.user.mapper.CategoryMapper;
import com.pblog.user.service.CategoryService;
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


        return new PageResult(
                categoryPage.getTotal(),    // 总记录数
                categoryPage.getPages(),    // 总页数
                categoryPage.getCurrent(),  // 当前页码
                categoryPage.getSize(),     // 每页条数
                collect              // 当前页数据
        );
    }


    @Override
    public List<CategoryVO> getAll() {
        List<CategoryVO> list = categoryMapper.selectCategoryVO();
        return list;
    }
}
