package com.pblog.common.vo;

import lombok.Data;

@Data
public class CategoryVO {

    private Integer id;
    private String categoryName;
    private Integer parentId;
    private Integer orderNum;
    private String description;

}
