package com.bhnote.controller;


import com.bhnote.model.ro.UserLoginRequest;
import com.bhnote.model.ro.UserRegisterRequest;
import com.bhnote.service.UserService;
import com.bhnote.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Bingo
 * @since 2022-01-05
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/api/user/v1")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("register")
    public JsonData register(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest);
    }

    @ApiOperation("用户登录")
    @PostMapping("login")
    public JsonData login(@RequestBody UserLoginRequest userLoginRequest) {
        return userService.login(userLoginRequest);
    }

    @ApiOperation("查询个人信息")
    @GetMapping("detail")
    public JsonData detail() {
        return userService.getUserDetail();
    }

}