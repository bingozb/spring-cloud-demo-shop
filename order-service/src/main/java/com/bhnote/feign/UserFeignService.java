package com.bhnote.feign;

import com.bhnote.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author bingo
 * @date 2022/1/11
 */
@FeignClient(name = "user-service")
public interface UserFeignService {

    /**
     * 根据收货地址ID查找详情
     *
     * @param addressId 收货地址ID
     * @return 收货地址详情
     */
    @GetMapping("/api/address/v1/find/{address_id}")
    JsonData findById(@PathVariable("address_id") long addressId);
}
