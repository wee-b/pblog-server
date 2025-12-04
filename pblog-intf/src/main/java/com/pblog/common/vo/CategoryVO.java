package com.pblog.common.vo;

import lombok.Data;

@Data
public class CategoryVO {

    private Integer id;
    private String categoryName;
    private Integer parentId;
    private Integer orderNum;
    // 带有此标签的文章数量
    private Integer articleCount;
    private String description;

}
