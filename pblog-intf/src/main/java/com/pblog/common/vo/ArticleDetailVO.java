package com.pblog.common.vo;

import lombok.Data;

// 继承 ArticleVO，复用基础字段（id、title、summary 等）
@Data
public class ArticleDetailVO extends ArticleVO {
    // 仅新增 content 字段（父类已包含其他所有字段和 categories 列表）
    private String content;

}