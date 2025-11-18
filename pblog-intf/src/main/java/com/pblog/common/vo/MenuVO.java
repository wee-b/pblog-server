package com.pblog.common.vo;

import lombok.Data;

@Data
public class MenuVO {

    private Integer id;

    private String menuName;

    private Integer parentId;

    private Integer orderNum;

    // 路由地址
    private String path;

    // 组件路径
    private String component;

    // 是否为外链（0是 1否）
    private Integer isFrame;

    // 菜单类型（M目录 C菜单 F按钮）
    private String menuType;

    // 菜单状态（0显示 1隐藏）
    private String visible;

    // 菜单状态（1正常 0停用）
    private String status;

    // 权限标识（如content:article:list）
    private String perms;

    // 菜单图标
    private String icon;

    private String remark;
}
