package com.bhnote.model.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bingo
 * @date 2022/1/7
 */
@Data
@ApiModel("新增收货地址请求体")
public class AddAddressRequest {

    /**
     * 是否默认收货地址：0->否；1->是
     */
    @ApiModelProperty(value = "是否是默认收货地址, 0->否；1->是", example = "0")
    private Integer defaultStatus;

    /**
     * 收发货人姓名
     */
    @ApiModelProperty(value = "收发货人姓名", example = "Bingo")
    private String receiveName;

    /**
     * 收货人电话
     */
    @ApiModelProperty(value = "收货人电话", example = "13544185508")
    private String phone;

    /**
     * 省/直辖市
     */
    @ApiModelProperty(value = "省/直辖市", example = "广东省")
    private String province;

    /**
     * 市
     */
    @ApiModelProperty(value = "市", example = "深圳市")
    private String city;

    /**
     * 区
     */
    @ApiModelProperty(value = "区", example = "南山区")
    private String region;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址", example = "蛇口街道水湾1979花园3座2215")
    private String detailAddress;
}
