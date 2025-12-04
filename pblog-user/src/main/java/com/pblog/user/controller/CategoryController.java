package com.pblog.user.controller;

import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.dto.admin.CategoryDTO;
import com.pblog.common.entity.Category;
import com.pblog.common.result.PageResult;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.vo.CategoryVO;
import com.pblog.user.service.CategoryService;
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
     * 查询全部（用于首页）
     */
    @GetMapping("/all")
    public ResponseResult<List<CategoryVO>> getAll() {
        List<CategoryVO> lis = categoryService.getAll() ;
        log.info("categoryService.getAll:{}", lis);
        return ResponseResult.success(lis);
    }



    // ===================================  弃用接口  ===================================
    /**
     * 分页查询
     *
     * @return 查询结果
     */
//    @PostMapping("/pagequery")
    public ResponseResult<PageResult> queryByPage(@RequestBody PageQueryDTO pageQueryDTO) {
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
//    @GetMapping("/queryById/{id}")
    public ResponseResult<Category> queryById(@PathVariable("id") Integer id) {
        Category cate = categoryService.queryById(id);
        log.info("categoryService.queryById:{}", cate);
        return ResponseResult.success(cate);
    }

}

