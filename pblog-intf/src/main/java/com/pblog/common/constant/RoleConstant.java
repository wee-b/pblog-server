package com.pblog.common.constant;

/**
 * 核心角色常量类（与pb_role表id严格对应）
 */
public class RoleConstant {
    // 未登录用户（仅浏览）
    public static final Integer UNLOGIN_ROLE_ID = 1;
    public static final String UNLOGIN_ROLE_KEY = "ROLE_UNLOGIN";

    // 游客（注册未绑定身份）
    public static final Integer VISITOR_ROLE_ID = 2;
    public static final String VISITOR_ROLE_KEY = "ROLE_VISITOR";

    // 正常用户（绑定身份）
    public static final Integer NORMAL_USER_ROLE_ID = 3;
    public static final String NORMAL_USER_ROLE_KEY = "ROLE_USER";

    // 审核
    public static final Integer AUDITOR_ROLE_ID = 4;
    public static final String AUDITOR_ROLE_KEY = "ROLE_AUDITOR";

}