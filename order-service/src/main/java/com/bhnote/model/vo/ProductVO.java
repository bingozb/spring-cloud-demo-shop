package com.bhnote.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
@Data
public class ProductVO {

    /**
     * 商品表ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 库存
     */
    private Integer stock;
}
