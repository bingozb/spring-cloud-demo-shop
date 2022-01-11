package com.bhnote.service;

import com.bhnote.model.ro.UserLoginRequest;
import com.bhnote.model.ro.UserRegisterRequest;
import com.bhnote.utils.JsonData;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Bingo
 * @since 2022-01-05
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     * @return 接口响应
     */
    JsonData register(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @return 接口响应
     */
    JsonData login(UserLoginRequest userLoginRequest);

    /**
     * 查询用户个人信息
     *
     * @return 接口响应
     */
    JsonData getUserDetail();
}
