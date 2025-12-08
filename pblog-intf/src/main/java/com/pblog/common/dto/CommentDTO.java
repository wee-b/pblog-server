package com.pblog.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDTO {

    /**
     * 文章ID（关联pb_article）
     *  文章Id为0代表该评论为留言板评论
     */
    private Integer articleId;


    private Integer rootId;

    /**
     * 父评论ID（自关联）
     */
    @NotNull(message = "请求出错了")
    private Integer parentId;


    private String toReplayUsername;

    /**
     * 评论内容
     */
    @NotNull(message = "评论内容不能为空")
    private String content;
}
