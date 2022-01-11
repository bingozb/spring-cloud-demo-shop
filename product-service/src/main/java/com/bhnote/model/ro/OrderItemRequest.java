package com.bhnote.model.ro;

import lombok.Data;

/**
 * @author bingo
 * @date 2022/1/10
 */
@Data
public class OrderItemRequest {

    /**
     * 商品ID
     */
    private long productId;

    /**
     * 购买件数
     */
    private int buyNum;
}