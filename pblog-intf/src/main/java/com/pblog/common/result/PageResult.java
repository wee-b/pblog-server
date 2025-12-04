package com.pblog.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 封装分页查询结果
 * @author e
 * @since 2025-08-18 21:57:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {
    private long total; // 总记录数
    private long pages; // 总页数
    private long current; // 当前页码
    private long size; // 每页条数
    private List<?> records; // 分页数据
}
