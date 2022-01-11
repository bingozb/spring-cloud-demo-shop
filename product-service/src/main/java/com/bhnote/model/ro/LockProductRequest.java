package com.bhnote.model.ro;

import lombok.Data;

import java.util.List;

/**
 * @author bingo
 * @date 2022/1/10
 */
@Data
public class LockProductRequest {

    /**
     * 订单号
     */
    private String orderOutTradeNo;

    /**
     * 订单项
     */
    private List<OrderItemRequest> orderItemList;
}
