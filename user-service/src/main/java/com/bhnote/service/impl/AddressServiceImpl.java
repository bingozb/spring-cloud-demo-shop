package com.bhnote.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bhnote.enums.BizCodeEnum;
import com.bhnote.interceptor.LoginInterceptor;
import com.bhnote.mapper.AddressMapper;
import com.bhnote.model.AddressDO;
import com.bhnote.model.ro.AddAddressRequest;
import com.bhnote.model.vo.AddressVO;
import com.bhnote.service.AddressService;
import com.bhnote.utils.JsonData;
import com.bhnote.utils.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 收发货地址表 服务实现类
 * </p>
 *
 * @author Bingo
 * @since 2022-01-07
 */
@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;

    @Override
    public JsonData add(AddAddressRequest addAddressRequest) {
        Long userId = LoginInterceptor.threadLocal.get().getId();
        AddressDO addressDO = new AddressDO();
        BeanUtils.copyProperties(addAddressRequest, addressDO);
        addressDO.setId(SnowFlakeUtil.getInstance().nextId());
        addressDO.setUserId(userId);
        // 如果保存默认地址，则判断是否已经有默认收货地址，如有，则需要更新原默认地址为非默认
        if (addAddressRequest.getDefaultStatus() == 1) {
            AddressDO defaultAddress = addressMapper.selectOne(Wrappers.lambdaQuery(AddressDO.class).eq(AddressDO::getUserId, userId).eq(AddressDO::getDefaultStatus, 1));
            if (defaultAddress != null) {
                defaultAddress.setDefaultStatus(0);
                addressMapper.updateById(defaultAddress);
            }
        }
        int rows = addressMapper.insert(addressDO);
        if (rows == 1) {
            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(addressDO, addressVO);
            return JsonData.buildSuccess(addressVO);
        }
        return JsonData.buildError(BizCodeEnum.ADDRESS_ADD_FAIL);
    }

    @Override
    public JsonData getById(Long addressId) {
        AddressDO addressDO = addressMapper.selectOne(Wrappers.lambdaQuery(AddressDO.class).eq(AddressDO::getId, addressId).eq(AddressDO::getUserId, LoginInterceptor.threadLocal.get().getId()));
        if (addressDO == null) {
            return JsonData.buildError(BizCodeEnum.ADDRESS_NO_EXITS);
        }
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(addressDO, addressVO);
        return JsonData.buildSuccess(addressVO);
    }

    @Override
    public JsonData delById(Long addressId) {
        AddressDO addressDO = addressMapper.selectOne(Wrappers.lambdaQuery(AddressDO.class).eq(AddressDO::getId, addressId).eq(AddressDO::getUserId, LoginInterceptor.threadLocal.get().getId()));
        if (addressDO == null) {
            return JsonData.buildError(BizCodeEnum.ADDRESS_NO_EXITS);
        }
        int rows = addressMapper.deleteById(addressId);
        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildError(BizCodeEnum.ADDRESS_DEL_FAIL);
    }

    @Override
    public JsonData list() {
        List<AddressDO> addressDOList = addressMapper.selectList(Wrappers.lambdaQuery(AddressDO.class).eq(AddressDO::getUserId, LoginInterceptor.threadLocal.get().getId()));
        List<AddressVO> addressVOList = addressDOList.stream().map(obj -> {
            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(obj, addressVO);
            return addressVO;
        }).collect(Collectors.toList());
        return JsonData.buildSuccess(addressVOList);
    }
}
