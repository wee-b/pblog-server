package com.pblog.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author e
 * @since 2025-08-18 21:57:38
 */

@Data
public class CategoryDto implements Serializable {

    private String name;
    private String alias;
    private Long parentId;
    private Integer sortOrder;
    private String description;
}
