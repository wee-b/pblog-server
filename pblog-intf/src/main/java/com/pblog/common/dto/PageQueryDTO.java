package com.pblog.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pblog.common.validator.TimeRange;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 通用分页查询 DTO（适配文章/列表类查询，支持多场景筛选）
 */
@Data
public class PageQueryDTO {

    /**
     * 当前页码（必填，默认1，最小值1）
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum = 1;

    /**
     * 每页条数（必填，默认10，1-50之间）
     */
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 50, message = "每页条数不能超过50")
    private Integer pageSize = 10;


    /**
     * 发布开始时间（可选）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 发布结束时间（可选）
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;


    /**
     * 排序方向（可选，默认降序）
     */
    @Pattern(regexp = "^(asc|desc)?$", message = "排序方向不合法，仅支持asc(升序)、desc(降序)")
    private String sortDir = "desc";

    /**
     * 自定义校验：开始时间不能晚于结束时间
     * 说明：需配合 @Valid 触发，且需要自定义校验器（下方提供实现）
     */
    @TimeRange(message = "开始时间不能晚于结束时间")
    public interface TimeRangeCheck {}
}