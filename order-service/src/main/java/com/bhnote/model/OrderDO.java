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
 * 订单表
 * </p>
 *
 * @author Bingo
 * @since 2022-01-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("`order`")
public class OrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 订单流水号
     */
    private String outTradeNo;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * NEW未支付, PAY已支付, CANCEL已取消
     */
    private String state;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
