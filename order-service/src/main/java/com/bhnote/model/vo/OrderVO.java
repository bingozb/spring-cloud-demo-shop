package com.bhnote.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author bingo
 * @date 2022/1/11
 */
@Data
public class OrderVO {

    private Long id;

    /**
     * 订单唯一标识
     */
    private String outTradeNo;

    /**
     * NEW未支付,PAY已支付,CANCEL已取消
     */
    private String state;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 收货地址
     */
    private OrderAddressVO receiverAddress;

    /**
     * 订单商品项
     */
    private List<OrderItemVO> orderItems;
}