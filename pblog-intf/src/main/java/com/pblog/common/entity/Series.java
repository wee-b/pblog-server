package com.pblog.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章系列表
 * @TableName pb_series
 */
@TableName(value = "pb_series")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Series implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系列ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 系列名称
     */
    private String seriesName;

    /**
     * 系列封面URL
     */
    private String coverImage;

    /**
     * 系列简介
     */
    private String description;

    /**
     * 系列状态（0正常 1禁用）
     */
    private String status;

    /**
     * 创建者ID（关联pb_user）
     */
    private Integer authorId;

    /**
     * 删除标志（0存在 1删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.UPDATE)
    private Integer updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}