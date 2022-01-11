package com.bhnote.service;

import com.bhnote.model.ro.ConfirmOrderRequest;
import com.bhnote.utils.JsonData;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author Bingo
 * @since 2022-01-11
 */
public interface OrderService {

    /**
     * 获取订单提交令牌
     *
     * @return 订单提交令牌
     */
    JsonData getToken();

    /**
     * 提交订单
     *
     * @param orderRequest 订单请求体
     * @return 接口响应
     */
    JsonData confirmOrder(ConfirmOrderRequest orderRequest);

    /**
     * 分页查询我的订单列表
     *
     * @param page  页数
     * @param size  条数
     * @param state 订单状态
     * @return 接口响应
     */
    JsonData pageUserOrderList(Integer page, Integer size, String state);

    /**
     * 查询订单状态
     *
     * @param outTradeNo 订单号
     * @return 接口响应
     */
    JsonData queryOrderState(String outTradeNo);

    /**
     * 关闭订单
     *
     * @param outTradeNo 订单号
     * @return 是否成功
     */
    boolean closeOrder(String outTradeNo);
}
