package com.pblog.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系列与文章关联表
 * @TableName pb_sa_relation
 */
@TableName(value = "pb_sa_relation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关联ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 系列ID（关联pb_series）
     */
    private Integer seriesId;

    /**
     * 文章ID（关联pb_article）
     */
    private Integer articleId;

    /**
     * 排序顺序（值越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 删除标志（0存在 1删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}