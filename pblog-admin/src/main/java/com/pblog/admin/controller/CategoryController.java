package com.pblog.admin.controller;

import com.pblog.common.vo.CategoryVO;
import com.pblog.common.dto.admin.CategoryDTO;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.entity.Category;
import com.pblog.common.result.PageResult;
import com.pblog.common.result.ResponseResult;
import com.pblog.admin.service.CategoryService;
import jakarta.validation.Valid;
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
@RequestMapping("/category")
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
    @GetMapping("/all")
    public ResponseResult<List<CategoryVO>> getAll() {
        List<CategoryVO> lis = categoryService.getAll() ;
        log.info("categoryService.getAll:{}", lis);
        return ResponseResult.success(lis);
    }
    /**
     * 分页查询
     *
     * @return 查询结果
     */
    @GetMapping("/pagequery")
    public ResponseResult<PageResult> queryByPage(@Valid @RequestBody PageQueryDTO pageQueryDTO) {
        PageResult pageResult = categoryService.queryByPage(pageQueryDTO);
        log.info("categoryService.pageResult:{}", pageResult);
        return ResponseResult.success(pageResult);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/queryById/{id}")
    public ResponseResult<Category> queryById(@PathVariable("id") Integer id) {
        Category cate = categoryService.queryById(id);
        log.info("categoryService.queryById:{}", cate);
        return ResponseResult.success(cate);
    }

    /**
     * 新增数据
     *
     * @param categoryDto 实体
     * @return 新增结果
     */
    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody CategoryDTO categoryDto) {
        categoryService.add(categoryDto);
        log.info("categoryService.add新增分类成功：{}", categoryDto);
        return ResponseResult.success();
    }

    /**
     * 编辑数据
     *
     * @param categoryDto 实体
     * @return 编辑结果
     */
    @PostMapping("/edit/{id}")
    public ResponseResult<String> edit(@PathVariable("id") Integer id,@RequestBody CategoryDTO categoryDto) {
        categoryService.edit(id,categoryDto);
        log.info("categoryService.edit分类修改成功：{}", categoryDto);
        return ResponseResult.success();
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("/delete/{id}")
    public ResponseResult<String> deleteById(@PathVariable("id") Integer id) {
        categoryService.deleteById(id);
        log.info("categoryService.delete分类已删除：{}", id);
        return ResponseResult.success();
    }

}

