package com.pblog.common.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentFromMeVO{

    private Integer id;


    private Integer articleId;

    // pb_article表
    private String articleTitle;


    /**
     * 父评论ID（自关联）
     */
    private Integer parentId;

    /**
     * 发布父评论的人的账号
     */
    private String toReplyUsername;

    /**
     * 评论内容
     */
    private String content;
    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;



}
