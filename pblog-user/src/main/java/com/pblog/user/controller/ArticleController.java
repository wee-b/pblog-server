package com.pblog.user.controller;

import com.pblog.common.dto.Article.ArticleDTO;
import com.pblog.common.dto.Article.ArticlePageQueryDTO;
import com.pblog.common.dto.Article.updateArticleDTO;
import com.pblog.common.result.PageResult;
import com.pblog.common.result.ResponseResult;
import com.pblog.common.vo.ArticleDetailVO;
import com.pblog.common.vo.ArticleVO;
import com.pblog.user.service.ArticleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客文章表(Article)表控制层（适配 MyBatis-Plus）
 *
 * @author makejava
 * @since 2025-11-29 16:20:52
 */
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
     * 发布文章
     */
    @PostMapping("/insert")
    public ResponseResult<Integer> insert(@Valid @RequestBody ArticleDTO articledto) {
        Integer res = articleService.insert(articledto,"/insert");
        log.info("articleService.add:{}", articledto);
        return ResponseResult.success(res ) ;
    }

    /**
     * 保存文章为草稿
     */
    @PostMapping("/insertDraft")
    public ResponseResult<Integer> insertDraft(@Valid @RequestBody ArticleDTO articledto) {
        Integer res = articleService.insert(articledto,"/insertDraft");
        log.info("articleService.insert:{}", articledto);
        return ResponseResult.success(res ) ;
    }

    /**
     * 修改数据
     */
    @PutMapping("/update")
    public ResponseResult<String> update(@Valid @RequestBody updateArticleDTO articledto) {
        boolean success = articleService.update(articledto);
        log.info("articleService.update:{}", articledto);
        return ResponseResult.success(success?"修改成功":"修改失败") ;
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
     * 下架/投稿文章
     */
    @PostMapping("/status/{id}")
    public ResponseResult<String> status(@PathVariable("id") Integer id) {
        boolean success = articleService.status(id);
        log.info("articleService.unAccessArticle:{}", id);
        return ResponseResult.success(success?"下架/投稿文章成功":"下架/投稿文章失败") ;
    }

    /**
     * 设为置顶/取消置顶
     */
    @PostMapping("/setSticky/{id}")
    public ResponseResult<String> setSticky(@PathVariable("id") Integer id) {
        boolean success = articleService.setSticky(id);
        log.info("articleService.setSticky:{}", id);
        return ResponseResult.success(success?"操作成功":"操作失败") ;
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
