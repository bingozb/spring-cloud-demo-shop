package com.bhnote.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分页查询统一返回Json对象
 *
 * @author bingo
 * @date 2022/1/10
 */
@Data
@Builder
public class PageResult {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 当前页查询结果
     */
    private List<?> list;
}