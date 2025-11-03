package com.pblog.common.entity.rabc;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色和菜单关联表
 * @TableName pb_role_menu
 */
@TableName(value = "pb_role_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PbRoleMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 菜单ID
     */
    private Integer menuId;
}