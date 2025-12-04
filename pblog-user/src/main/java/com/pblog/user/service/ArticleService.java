package com.pblog.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pblog.common.dto.Article.ArticleDTO;
import com.pblog.common.dto.Article.ArticlePageQueryDTO;
import com.pblog.common.dto.Article.updateArticleDTO;
import com.pblog.common.entity.Article;
import com.pblog.common.result.PageResult;
import com.pblog.common.vo.ArticleDetailVO;
import com.pblog.common.vo.ArticleVO;

import java.util.List;

/**
 * 博客文章表(Article)表服务接口
 */
public interface ArticleService extends IService<Article> {

    PageResult pageQuery(ArticlePageQueryDTO pageQueryDTO);

    Integer insert(ArticleDTO articledto,String url);

    boolean update(updateArticleDTO articledto);

    boolean status(Integer id);

    boolean setSticky(Integer id);

    boolean setFeatured(Integer id);

    List<ArticleVO> getFeaturedArticles();

    ArticleDetailVO queryById(Integer id);
}


