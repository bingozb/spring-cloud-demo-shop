package com.bhnote.service;

import com.bhnote.model.dto.OrderLockMessage;
import com.bhnote.model.ro.LockProductRequest;
import com.bhnote.model.ro.SaveProductRequest;
import com.bhnote.model.vo.ProductVO;
import com.bhnote.utils.JsonData;

import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
public interface ProductService {

    /**
     * 保存商品
     *
     * @param saveProductRequest 保存商品请求体
     * @return 接口响应
     */
    JsonData saveProduct(SaveProductRequest saveProductRequest);

    /**
     * 分页获取商品列表
     *
     * @param page 页数
     * @param size 条数
     * @return 接口响应
     */
    JsonData getPageProduct(long page, long size);

    /**
     * 根据ID查找商品详情
     *
     * @param productId 商品ID
     * @return 接口响应
     */
    JsonData getProductDetail(long productId);

    /**
     * 锁定商品库存
     *
     * @param lockProductRequest 锁定商品请求体
     * @return 接口响应
     */
    JsonData lockProductStock(LockProductRequest lockProductRequest);

    /**
     * 批量获取指定的商品信息
     * @param productIds 商品ID数组
     * @return 接口响应
     */
    List<ProductVO> getBatchProduct(List<Long> productIds);

    /**
     * 释放商品库存
     *
     * @param orderLockTaskMessage 锁定任务消息对象
     * @return 是否消费成功
     */
    boolean releaseProductStock(OrderLockMessage orderLockTaskMessage);
}
