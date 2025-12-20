package com.pblog.common.dto.Article;

import com.pblog.common.dto.PageQueryDTO;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ArticlePageQueryDTO extends PageQueryDTO {


    /**
     * 多分类ID筛选（可选，元素需为正整数）
     * 场景：筛选多个分类下的内容，集合为空则不筛选
     */
    @Size(max = 10, message = "最多支持筛选10个分类")
    private List<@Min(value = 1, message = "分类ID必须为正整数") Integer> categoryIds;

    /**
     * 关键词模糊查询（可选）
     */
    @Size(max = 50, message = "关键词长度不能超过50个字符")
    private String keyword;

    /**
     * 以文章状态（-1查询所有 0草稿 1已发布 2待审核）
     */
    @Size(max = 2, message = "参数错误")
    private String status;

    /**
     * 排序字段（可选，仅支持指定合法字段）
     * 说明：限制排序字段为系统支持的类型，避免SQL注入风险
     */
    @Pattern(regexp = "^(publishedAt|viewCount|likeCount|commentCount)?$",
            message = "排序字段不合法，支持：publishedAt(发布时间)、viewCount(阅读量)、likeCount(点赞数)、commentCount(评论数)")
    private String sortField;

    private String username;

}
