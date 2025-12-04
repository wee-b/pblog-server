package com.pblog.common.dto.Article;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;


@Data
public class ArticleDTO {

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题长度不能超过200字")
    private String title;
    /**
     * 文章正文
     */
    @NotBlank(message = "文章正文不能为空")
    private String content;
    /**
     * 文章摘要
     */
    @Size(max = 500, message = "文章摘要长度不能超过500字")
    private String summary;
    /**
     * 文章封面URL
     */
    private String coverImage;


    // 文章标签
    private List<Integer> tagIds;

}
