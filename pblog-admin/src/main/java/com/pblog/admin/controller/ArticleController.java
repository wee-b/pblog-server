package com.pblog.admin.controller;

import com.pblog.common.dto.Article.ArticleDTO;
import com.pblog.common.dto.Article.ArticlePageQueryDTO;
import com.pblog.common.dto.Article.updateArticleDTO;
import com.pblog.common.result.PageResult;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.vo.ArticleDetailVO;
import com.pblog.common.vo.ArticleVO;
import com.pblog.admin.service.ArticleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/article") 
public class ArticleController {
    /**
     * 服务对象（注入 MP 的 IService 接口，而非实现类）
     */
    @Autowired
    private ArticleService articleService;

    /**
     * 分页查询所有数据
     */
    @PostMapping("/pageQuery")
    public ResponseResult<PageResult> pageQuery(@Valid @RequestBody ArticlePageQueryDTO pageQueryDTO) {
        PageResult pageResult = articleService.pageQuery(pageQueryDTO);
        log.info("articleService.pageResult:{}", pageResult);
        return ResponseResult.success(pageResult);
    }

    /**
     * 通过主键查询单条数据
     */
    @GetMapping("/queryById/{id}")
    public ResponseResult<ArticleDetailVO> queryById(@PathVariable("id") Integer id) {
        ArticleDetailVO one = articleService.queryById(id);
        log.info("articleService.queryById:{}", one);
        return ResponseResult.success(one);
    }



    /**
     * 删除数据（调用 MP removeById 方法）
     */
    @DeleteMapping("/delete/{id}")
    public ResponseResult<String> deleteById(@PathVariable("id") Integer id) {
        boolean success = articleService.removeById(id); // MP 内置方法
        log.info("articleService.deleteById:{}", id);
        return ResponseResult.success(success?"删除成功":"删除失败") ;
    }


    /**
     * 审核通过文章
     */
    @PostMapping("/statusPass/{id}")
    public ResponseResult<String> statusPass(@PathVariable("id") Integer id) {
        boolean success = articleService.statusPass(id);
        log.info("articleService.statusPass:{}", id);
        return ResponseResult.success(success?"审核文章成功":"审核文章失败") ;
    }


    /**
     * 下架/投稿文章
     */
    @PostMapping("/status/{id}")
    public ResponseResult<String> status(@PathVariable("id") Integer id) {
        boolean success = articleService.status(id);
        log.info("articleService.status:{}", id);
        return ResponseResult.success(success?"下架/投稿文章成功":"下架/投稿文章失败") ;
    }


    /**
     * 设为推荐/取消推荐
     */
    @PostMapping("/setFeatured/{id}")
    public ResponseResult<String> setFeatured(@PathVariable("id") Integer id) {
        boolean success = articleService.setFeatured(id);
        log.info("articleService.setFeatured:{}", id);
        return ResponseResult.success(success?"操作成功":"操作失败") ;
    }

    /**
     * 获取推荐文章
     */
    @GetMapping("/getFeaturedArticles")
    public ResponseResult<List<ArticleVO>> getFeaturedArticles() {
        List<ArticleVO> res = articleService.getFeaturedArticles();
        log.info("articleService.getFeaturedArticles:{}", res);
        return ResponseResult.success(res) ;
    }





}
