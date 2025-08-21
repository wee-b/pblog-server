package com.pblog.controller;

import com.pblog.dto.CategoryDto;
import com.pblog.entity.Category;
import com.pblog.result.PageResult;
import com.pblog.result.Result;
import com.pblog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 博客分类表，存储文章分类信息，支持多级分类(Category)表控制层
 *
 * @author makejava
 * @since 2025-08-18 17:19:14
 */

@Slf4j
@RestController
@RequestMapping("category/")
public class CategoryController {
    /**
     * 服务对象
     */
    @Autowired
    private CategoryService categoryService;


    /**
     * 查询全部
     *
     * @return 查询结果
     */
    @GetMapping("all/")
    public Result<List<Category>> getAll() {
        List<Category> lis = categoryService.getAll() ;
        log.info("categoryService.getAll:{}", lis);
        return Result.success(lis);
    }
    /**
     * 分页查询
     *
     * @param pageNo
     * @param pageSize
     * @return 查询结果
     */
//    @GetMapping("pagequery")
//    public Result<PageResult> queryByPage(Integer pageNo, Integer pageSize) {
//        PageResult pageResult = categoryService.queryByPage(pageNo,pageSize);
//        log.info("categoryService.pageResult:{}", pageResult);
//        return Result.success(pageResult);
//    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("queryById/{id}")
    public Result<Category> queryById(@PathVariable("id") Integer id) {
        Category cate = categoryService.queryById(id);
        log.info("categoryService.queryById:{}", cate);
        return Result.success(cate);
    }

    /**
     * 新增数据
     *
     * @param categoryDto 实体
     * @return 新增结果
     */
    @PostMapping("add/")
    public Result<String> add(@RequestBody CategoryDto categoryDto) {
        categoryService.add(categoryDto);
        log.info("categoryService.add新增分类成功：{}", categoryDto);
        return Result.success();
    }

    /**
     * 编辑数据
     *
     * @param categoryDto 实体
     * @return 编辑结果
     */
    @PutMapping("edit/")
    public Result<String> edit(@RequestBody CategoryDto categoryDto) {
        categoryService.edit(categoryDto);
        log.info("categoryService.edit分类修改成功：{}", categoryDto);
        return Result.success();
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("delete/{id}")
    public Result<String> deleteById(@PathVariable("id") Integer id) {
        categoryService.deleteById(id);
        log.info("categoryService.delete分类已删除：{}", id);
        return Result.success();
    }

}

