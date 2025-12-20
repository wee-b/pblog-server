package com.pblog.admin.vo;

import com.pblog.common.vo.CommentVO;
import lombok.Data;

@Data
public class CommentDetailVO extends CommentVO {

    // articleId=0代表评论来自留言板
    private Integer articleId;

    // 在pb_article中的title字段
    private String articleTitle;

    private String status;
}
