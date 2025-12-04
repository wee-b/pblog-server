package com.pblog.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class AcRelationVO {

    private Integer articleId;
    private List<CategoryVO> categoryList;
}
