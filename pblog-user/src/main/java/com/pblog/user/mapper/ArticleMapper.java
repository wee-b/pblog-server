package com.pblog.user.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.vo.ArticleDetailVO;
import com.pblog.common.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 博客文章表(Article)表数据库访问层
 *
 * @author makejava
 * @since 2025-11-29 01:25:36
 */

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

     /**
      * 自定义分页查询（XML 实现，支持多条件筛选+排序）
      * @param page 分页参数（MP 分页对象，自动处理分页）
      * @param queryDTO 筛选条件（所有查询参数封装）
      * @return 带分页信息的结果
      */
     IPage<ArticleVO> selectArticlePage(
             @Param("page") Page<ArticleVO> page,
             @Param("query") PageQueryDTO queryDTO
     );

     List<ArticleVO> selectFeaturedArticle();

     ArticleDetailVO getArticleDetail(Integer articleId);
}
