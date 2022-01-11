package com.bhnote.controller;


import com.bhnote.model.ro.ConfirmOrderRequest;
import com.bhnote.service.OrderService;
import com.bhnote.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author Bingo
 * @since 2022-01-11
 */
@Api(tags = "订单模块")
@RestController
@RequestMapping("/api/order/v1")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    @ApiOperation("获取订单提交令牌")
    @GetMapping("token")
    public JsonData getToken() {
        return orderService.getToken();
    }

    @ApiOperation("提交订单")
    @PostMapping("confirm")
    public JsonData confirmOrder(@ApiParam("订单对象") @RequestBody ConfirmOrderRequest orderRequest) {
        return orderService.confirmOrder(orderRequest);
    }

    @ApiOperation("分页查询我的订单列表")
    @GetMapping("page_list")
    public JsonData pageMyOrder(@ApiParam(value = "页数") @RequestParam(defaultValue = "1") Integer page,
                                @ApiParam(value = "条数") @RequestParam(defaultValue = "10") Integer size,
                                @ApiParam(value = "状态") @RequestParam(required = false) String state) {
        return orderService.pageUserOrderList(page, size, state);
    }

    @ApiOperation("RPC-查询订单状态")
    @GetMapping("query_state")
    public JsonData queryOrderState(@ApiParam("订单号") @RequestParam("out_trade_no") String outTradeNo) {
        return orderService.queryOrderState(outTradeNo);
    }
}

