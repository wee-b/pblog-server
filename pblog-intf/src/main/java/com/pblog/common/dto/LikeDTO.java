package com.pblog.common.dto;

import lombok.Data;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;


@Data
public class LikeDTO {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Integer targetId;
    /**
     * 内容类型：0-其他，1-文章，2-评论
     */
    @NotBlank(message = "内容类型不能为空")
    private String targetType;
    /**
     * 操作类型：1-点赞，0-取消点赞
     */
    @NotBlank(message = "操作类型不能为空")
    private String operateType;

    // 前端不传递此字段
    private String username;
    private String timeStrap;
}