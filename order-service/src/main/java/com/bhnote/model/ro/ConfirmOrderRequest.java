package com.bhnote.model.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bingo
 * @date 2022/1/11
 */
@Data
@ApiModel("提交订单请求体")
public class ConfirmOrderRequest {

    /**
     * 购买的商品列表
     */
    @ApiModelProperty(value = "购买的商品列表")
    private List<OrderItemRequest> productList;

    /**
     * 收货地址id
     */
    @ApiModelProperty(value = "收货地址ID")
    private long addressId;

    /**
     * 防重令牌
     */
    @ApiModelProperty(value = "防重令牌")
    private String token;
}