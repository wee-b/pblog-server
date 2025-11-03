package com.pblog.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章与分类关联表
 * @TableName pb_ac_relation
 */
@TableName(value = "pb_ac_relation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关联ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文章ID（关联pb_article）
     */
    private Integer articleId;

    /**
     * 分类ID（关联pb_category）
     */
    private Integer categoryId;

    /**
     * 删除标志（0存在 1删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 创建者
     */
    private Integer createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}