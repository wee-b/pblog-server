package com.pblog.admin.query;

import com.pblog.common.dto.PageQueryDTO;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class commentPageQueryDTO extends PageQueryDTO {

    /**
     * 评论状态(-1 表示查询全部)（0待审核 1已通过 2已驳回）
     */
    @Size(max = 2, message = "参数错误")
    private String status;


    private Integer articleId;
}
