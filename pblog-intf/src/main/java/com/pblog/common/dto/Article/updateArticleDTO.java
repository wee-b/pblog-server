package com.pblog.common.dto.Article;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class updateArticleDTO extends ArticleDTO{

    @NotNull(message = "文章ID不能为空")
    private Integer id;


}
