package com.bhnote.model.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bingo
 * @date 2022/1/6
 */
@Data
@ApiModel("用户登录请求体")
public class UserLoginRequest {

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", example = "bingov5@icloud.com")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", example = "123456")
    private String password;
}
