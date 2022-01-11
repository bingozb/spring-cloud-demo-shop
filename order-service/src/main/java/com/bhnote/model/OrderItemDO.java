package com.bhnote.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单项表
 * </p>
 *
 * @author Bingo
 * @since 2022-01-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_item")
public class OrderItemDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单项ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 订单流水号
     */
    private String outTradeNo;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer buyNum;

    /**
     * 购物项总价格
     */
    private BigDecimal totalPrice;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
