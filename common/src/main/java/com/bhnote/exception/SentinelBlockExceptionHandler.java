package com.bhnote.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.bhnote.enums.BizCodeEnum;
import com.bhnote.utils.HttpServletResponseUtil;
import com.bhnote.utils.JsonData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sentinel 限流异常处理，返回json信息
 *
 * @author bingo
 * @date 2022/1/6
 */
@Component
public class SentinelBlockExceptionHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) {

        JsonData jsonData = null;
        if (e instanceof FlowException) {
            jsonData = JsonData.buildError(BizCodeEnum.CONTROL_FLOW_ERROR);
        } else if (e instanceof DegradeException) {
            jsonData = JsonData.buildError(BizCodeEnum.CONTROL_DEGRADE_ERROR);
        } else if (e instanceof AuthorityException) {
            jsonData = JsonData.buildError(BizCodeEnum.CONTROL_AUTH_ERROR);
        } else if (e instanceof ParamFlowException) {
            jsonData = JsonData.buildError(BizCodeEnum.CONTROL_PARAM_FLOW_ERROR);
        } else if (e instanceof SystemBlockException) {
            jsonData = JsonData.buildError(BizCodeEnum.CONTROL_SYSTEM_BLOCK_ERROR);
        }
        HttpServletResponseUtil.responseJson(httpServletResponse, jsonData);
    }
}