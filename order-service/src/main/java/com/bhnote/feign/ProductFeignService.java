package com.bhnote.feign;

import com.bhnote.model.ro.LockProductRequest;
import com.bhnote.model.vo.ProductVO;
import com.bhnote.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author bingo
 * @date 2022/1/11
 */
@FeignClient(name = "product-service")
public interface ProductFeignService {

    /**
     * 批量获取指定的商品信息
     *
     * @param productIds 商品ID数组
     * @return 商品列表
     */
    @PostMapping("/api/product/v1/batch_list")
    List<ProductVO> getBatchProduct(@RequestBody List<Long> productIds);

    /**
     * 锁定商品库存
     *
     * @param lockProductRequest 锁定商品库存请求体
     * @return 接口响应
     */
    @PostMapping("/api/product/v1/lock")
    JsonData lockProductStock(@RequestBody LockProductRequest lockProductRequest);
}
