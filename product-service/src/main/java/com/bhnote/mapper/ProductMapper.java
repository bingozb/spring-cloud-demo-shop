package com.bhnote.mapper;

import com.bhnote.model.ProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
public interface ProductMapper extends BaseMapper<ProductDO> {

    /**
     * 锁定商品库存
     *
     * @param productId 商品ID
     * @param buyNum    购买数量
     * @return 是否锁定成功
     */
    int lockProductStock(@Param("productId") long productId, @Param("buyNum") long buyNum);

    /**
     * 释放商品库存
     *
     * @param productId 商品ID
     * @param buyNum    购买数量
     * @return 是否释放成功
     */
    int releaseProductStock(@Param("productId") long productId, @Param("buyNum") long buyNum);
}
