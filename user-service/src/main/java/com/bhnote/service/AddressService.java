package com.bhnote.service;


import com.bhnote.model.ro.AddAddressRequest;
import com.bhnote.utils.JsonData;

/**
 * <p>
 * 收发货地址表 服务类
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
public interface AddressService {

    /**
     * 新增收货地址
     *
     * @param addAddressRequest 收货地址
     * @return 接口响应
     */
    JsonData add(AddAddressRequest addAddressRequest);

    /**
     * 根据ID查找地址详情
     *
     * @param addressId 收货地址ID
     * @return 接口响应
     */
    JsonData getById(Long addressId);

    /**
     * 删除收货地址
     *
     * @param addressId 收货地址ID
     * @return 接口响应
     */
    JsonData delById(Long addressId);

    /**
     * 查找用户收货地址列表
     *
     * @return 接口响应
     */
    JsonData list();
}
