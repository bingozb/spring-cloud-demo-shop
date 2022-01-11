package com.bhnote.controller;


import com.bhnote.model.ro.AddAddressRequest;
import com.bhnote.service.AddressService;
import com.bhnote.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 收发货地址表 前端控制器
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
@Api(tags = "收货地址模块")
@RestController
@RequestMapping("/api/address/v1")
public class AddressController {

    @Resource
    private AddressService addressService;

    @ApiOperation("新增收货地址")
    @PostMapping("add")
    public JsonData add(@ApiParam("收货地址对象") @RequestBody AddAddressRequest addAddressRequest) {
        return addressService.add(addAddressRequest);
    }

    @ApiOperation(value = "根据收货地址ID查找详情")
    @GetMapping("/find/{address_id}")
    public JsonData findById(@ApiParam(value = "收货地址ID", required = true)
                             @PathVariable("address_id") long addressId) {
        return addressService.getById(addressId);
    }

    @ApiOperation(value = "删除收货地址")
    @DeleteMapping("/del/{address_id}")
    public JsonData del(@ApiParam(value = "收货地址ID", required = true)
                        @PathVariable("address_id") long addressId) {
        return addressService.delById(addressId);
    }

    @ApiOperation("查找收货地址列表")
    @GetMapping("list")
    public JsonData list() {
        return addressService.list();
    }

}

