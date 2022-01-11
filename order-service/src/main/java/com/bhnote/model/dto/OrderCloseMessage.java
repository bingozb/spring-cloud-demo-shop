package com.bhnote.model.dto;

import lombok.Data;

/**
 * @author bingo
 * @date 2022/1/11
 */
@Data
public class OrderCloseMessage {

    /**
     * 订单号
     */
    private String outTradeNo;
}
