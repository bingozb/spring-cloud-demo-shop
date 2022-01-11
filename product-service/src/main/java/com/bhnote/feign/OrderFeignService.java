package com.bhnote.feign;

import com.bhnote.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author bingo
 * @date 2022/1/10
 */
@FeignClient(name = "order-service")
public interface OrderFeignService {

    /**
     * 查询订单状态
     *
     * @param outTradeNo 订单流水号
     * @return 订单状态
     */
    @GetMapping("/api/order/v1/query_state")
    JsonData queryOrderState(@RequestParam("out_trade_no") String outTradeNo);
}
