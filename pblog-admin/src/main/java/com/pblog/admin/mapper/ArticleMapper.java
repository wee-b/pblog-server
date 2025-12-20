package com.pblog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pblog.common.dto.Article.ArticlePageQueryDTO;
import com.pblog.common.entity.Article;
import com.pblog.common.vo.ArticleDetailVO;
import com.pblog.common.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;



@Mapper
public interface ArticleMapper extends BaseMapper<Article> {


     IPage<ArticleVO> selectArticlePage(
             @Param("page") Page<ArticleVO> page,
             @Param("query") ArticlePageQueryDTO queryDTO
     );

     List<ArticleVO> selectFeaturedArticle();

     ArticleDetailVO getArticleDetail(Integer articleId);
}
