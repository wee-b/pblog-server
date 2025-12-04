package com.pblog.common.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleVO {

    private Integer id;
    //标题
    private String title;
    //文章摘要
    private String summary;

    private String coverImage;

    private String authorUsername;
    private String authorNickName;

    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;

    /**
    * 是否置顶（0否 1是）
    */
    private String sticky;
    /**
     * 是否推荐（0否 1是）
     */
    private String featured;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;


    private List<CategoryVO> categories;

    // （0草稿 1已发布 2待审核）
    private String status;

}
