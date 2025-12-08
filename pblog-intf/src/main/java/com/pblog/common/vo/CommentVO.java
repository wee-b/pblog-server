package com.pblog.common.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    private Integer id;

    private String toReplyUsername;


    private Integer rootId;

    /**
     * 父评论ID（自关联）
     */
    private Integer parentId;

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


    // pb_user表:不返回bio
    private UserInfoVO userInfoVO;

}
