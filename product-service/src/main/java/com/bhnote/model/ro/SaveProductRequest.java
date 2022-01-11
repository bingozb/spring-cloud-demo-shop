package com.bhnote.model.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bingo
 * @date 2022/1/10
 */
@Data
@ApiModel("保存商品请求体")
public class SaveProductRequest {

    @ApiModelProperty(value = "商品ID")
    private Long id;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称", example = "商品名称示例")
    private String name;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格", example = "100.00")
    private BigDecimal price;

    /**
     * 库存
     */
    @ApiModelProperty(value = "库存", example = "100")
    private Integer stock;
}
