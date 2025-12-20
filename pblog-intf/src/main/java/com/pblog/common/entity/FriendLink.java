package com.pblog.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 友情链接表
 * @TableName pb_friend_link
 */
@TableName(value = "pb_friend_link")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendLink implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 友链ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站URL
     */
    private String siteUrl;

    /**
     * 网站LOGO
     */
    private String logo;

    /**
     * 网站简介
     */
    private String description;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 状态（0待审核 1已通过 2已拒绝）
     */
    private String status;

    /**
     * 显示顺序
     */
    private Integer sortOrder;

    /**
     * 审核人ID（关联pb_user）
     */
    private Integer approvedBy;

    /**
     * 删除标志（0存在 1删除）
     */
//    @TableLogic
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
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}