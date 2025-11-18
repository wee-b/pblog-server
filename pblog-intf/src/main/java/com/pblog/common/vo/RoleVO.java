package com.pblog.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleVO {

    private Integer id;
    private String roleName;
    private String roleKey;
    private Integer roleSort;
    private String status;
    private List<Integer> menuIds;
    private String remark;
}
