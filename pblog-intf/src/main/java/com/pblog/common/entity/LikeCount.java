package com.pblog.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("pb_like_counts")
public class LikeCount implements Serializable {
    /**
     * 内容ID
     */
    @TableId
    private Integer targetId;
    /**
     * 内容类型：0-其他，1-文章，2-评论
     */
    private String targetType;
    /**
     * 点赞总数
     */
    private Integer total;
}
