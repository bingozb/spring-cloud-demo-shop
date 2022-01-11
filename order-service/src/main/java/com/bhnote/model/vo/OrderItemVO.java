package com.bhnote.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bingo
 * @date 2022/1/11
 */
@Data
public class OrderItemVO {

    private Long id;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 购买数量
     */
    private Integer buyNum;

    /**
     * 购物项商品单价
     */
    private BigDecimal price;

    /**
     * 购物项商品总价格
     */
    private BigDecimal totalPrice;
}
