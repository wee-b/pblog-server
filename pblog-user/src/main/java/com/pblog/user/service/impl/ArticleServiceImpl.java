package com.pblog.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pblog.common.constant.DefaultConstants;
import com.pblog.common.dto.Article.ArticleDTO;
import com.pblog.common.dto.Article.ArticlePageQueryDTO;
import com.pblog.common.dto.Article.updateArticleDTO;
import com.pblog.common.entity.AcRelation;
import com.pblog.common.result.PageResult;
import com.pblog.common.utils.SecurityContextUtil;
import com.pblog.common.vo.AcRelationVO;
import com.pblog.common.vo.ArticleDetailVO;
import com.pblog.common.vo.ArticleVO;
import com.pblog.common.vo.CategoryVO;
import com.pblog.user.mapper.ACRelationMapper;
import com.pblog.user.mapper.ArticleMapper;
import com.pblog.common.entity.Article;
import com.pblog.user.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 博客文章表(Article)表服务实现类
 *
 * @author makejava
 * @since 2025-11-29 01:25:36
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ACRelationMapper acRelationMapper;

    @Override
    public PageResult pageQuery(ArticlePageQueryDTO pageQueryDTO) {
        // 1. 入参防御：默认分页参数（pageNum=1，pageSize=10）
        if (pageQueryDTO == null) {
            pageQueryDTO = new ArticlePageQueryDTO();
        }

        // 2. MP 分页对象：自动处理 limit 分页（依赖分页插件）
        Page<ArticleVO> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());

        // 3. 调用 MyBatis XML 自定义查询（SQL 逻辑在 XML 中，可读性强）
        IPage<ArticleVO> articlePage = articleMapper.selectArticlePage(page, pageQueryDTO);
        List<ArticleVO> articles = articlePage.getRecords();
        List<Integer> articleIds = articles.stream()
                .map(ArticleVO::getId).toList();

        // 4. 查询文章对应分类并直接构建 Map
        Map<Integer, List<CategoryVO>> articleIdToCategories = acRelationMapper
                .selectAcRelationVOByArticleIds(articleIds)
                .stream()
                .collect(Collectors.toMap(AcRelationVO::getArticleId, AcRelationVO::getCategoryList));

        // 5. 遍历文章列表一次就赋值
        articles.forEach(article ->
                article.setCategories(articleIdToCategories.getOrDefault(article.getId(), Collections.emptyList()))
        );

        // 6. 构建完整分页结果
        return new PageResult(
                articlePage.getTotal(),    // 总记录数
                articlePage.getPages(),    // 总页数
                articlePage.getCurrent(),  // 当前页码
                articlePage.getSize(),     // 每页条数
                articles                // 当前页数据
        );
    }

    /**
     * 保存草稿、发布文章
     */
    @Transactional
    @Override
    public Integer insert(ArticleDTO articledto,String url) {

        Article article = new Article();
        BeanUtils.copyProperties(articledto, article);

        String status = DefaultConstants.DEFAULT_STATUS;
        if(url == "/insertDraft"){
            status = DefaultConstants.Draft_Status;
            // 不保存coverImage
            article.setCoverImage("");
        }

        // 可调整
        article.setCommentCount(DefaultConstants.ZERO);
        article.setLikeCount(DefaultConstants.ZERO);
        article.setViewCount(DefaultConstants.ZERO);
        article.setDelFlag(DefaultConstants.DEFAULT_DELFLAG);
        article.setSticky(DefaultConstants.unSticky);
        article.setFeatured(DefaultConstants.unFeatured);
        article.setAuthorUsername(SecurityContextUtil.getUsername());
        article.setAuthorNickName(SecurityContextUtil.getUser().getNickname());
        article.setStatus(status);

        articleMapper.insert(article);

        if(articledto.getTagIds()!= null && articledto.getTagIds().size()>0){
            // 插入标签数据
            acRelationMapper.insertByCategoryIds(article.getId(),articledto.getTagIds());
        }


        return article.getId();
    }

    @Transactional
    @Override
    public boolean update(updateArticleDTO articledto) {

        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        // 可调整
        updateWrapper.eq(Article::getId, articledto.getId())
                .set(Article::getTitle, articledto.getTitle())
                .set(Article::getContent, articledto.getContent())
                .set(Article::getSummary, articledto.getSummary())
                .set(Article::getStatus, DefaultConstants.DEFAULT_STATUS);  // 更新后需要审核

        // 保存草稿不会携带coverImage
        if (articledto.getCoverImage() != null && !articledto.getCoverImage().equals("")) {
            updateWrapper.set(Article::getCoverImage, articledto.getCoverImage());
        }

        // getTagIds空指针判断
        if(articledto.getTagIds()!= null && articledto.getTagIds().size()>0){
            // 删除该文章的所有旧关联
            LambdaQueryWrapper<AcRelation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AcRelation::getArticleId, articledto.getId());
            acRelationMapper.delete(wrapper);
            // 再插入新关联
            acRelationMapper.insertByCategoryIds(articledto.getId(),articledto.getTagIds());
        }

        int rows = articleMapper.update(null, updateWrapper);
        return rows > 0;
    }

    @Override
    public boolean status(Integer id) {

        String status = "";
        Article one = articleMapper.selectById(id);

        if(one.getStatus().equals(DefaultConstants.toInspect)){
            // 待审核-->草稿  （取消投稿）
            status = DefaultConstants.Draft_Status;
        }else if(one.getStatus().equals(DefaultConstants.Already_handout)){
            // 已发布-->草稿  （下架）
            status = DefaultConstants.Draft_Status;
        }else if(one.getStatus().equals(DefaultConstants.Draft_Status)){
            // 草稿-->待审核  （投稿）
            status = DefaultConstants.toInspect;
        }


        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        // 更新条件：根据 id 定位（必须，否则会更新所有数据！）
        updateWrapper.eq(Article::getId, one.getId())
                .set(Article::getStatus,status);

        int rows = articleMapper.update(null, updateWrapper);
        return rows > 0;
    }

    @Override
    public boolean setSticky(Integer id) {
        Article one = articleMapper.selectById(id);
        // 可调整
        String banned = DefaultConstants.unSticky;      //1
        String unban = DefaultConstants.isSticky;

        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        // 更新条件：根据 id 定位（必须，否则会更新所有数据！）
        updateWrapper.eq(Article::getId, one.getId())
                .set(Article::getSticky, one.getSticky().equals(banned) ? unban : banned);

        int rows = articleMapper.update(null, updateWrapper);
        return rows > 0;
    }

    @Override
    public boolean setFeatured(Integer id) {
        Article one = articleMapper.selectById(id);
        // 可调整
        String banned = DefaultConstants.unFeatured;
        String unban = DefaultConstants.isFeatured;

        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        // 更新条件：根据 id 定位（必须，否则会更新所有数据！）
        updateWrapper.eq(Article::getId, one.getId())
                .set(Article::getFeatured, one.getFeatured().equals(banned) ? unban : banned);

        int rows = articleMapper.update(null, updateWrapper);
        return rows > 0;
    }

    @Override
    public List<ArticleVO> getFeaturedArticles() {
        List<ArticleVO> articlevos = articleMapper.selectFeaturedArticle();
        return articlevos;
    }

    @Override
    public ArticleDetailVO queryById(Integer id) {
        ArticleDetailVO article = articleMapper.getArticleDetail(id);
        return article;
    }


}

