package com.bhnote.model.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bingo
 * @date 2022/1/10
 */
@Data
@ApiModel("购买商品项")
public class OrderItemRequest {

    /**
     * 购买的商品
     */
    @ApiModelProperty(value = "购买的商品ID", example = "1480468316833189888")
    private long productId;

    /**
     * 购买的数量
     */
    @ApiModelProperty(value = "购买的数量", example = "1")
    private int buyNum;
}