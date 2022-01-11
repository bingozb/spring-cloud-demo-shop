package com.bhnote.model.dto;

import lombok.Data;

/**
 * 订单锁定消息对象
 *
 * @author bingo
 * @date 2022/1/10
 */
@Data
public class OrderLockMessage {

    /**
     * 商品锁定表ID
     */
    private Long lockId;

    /**
     * 订单号
     */
    private String outTradeNo;
}
