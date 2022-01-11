package com.bhnote.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 商品库存锁定表
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_lock")
public class ProductLockDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品库存锁定表
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 购买数量
     */
    private Integer buyNum;

    /**
     * 锁定状态 锁定LOCK 完成FINISH 取消CANCEL
     */
    private String lockState;

    /**
     * 订单流水号
     */
    private String outTradeNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
