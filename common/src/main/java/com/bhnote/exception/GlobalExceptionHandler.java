package com.bhnote.exception;

import com.bhnote.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 *
 * @author bingo
 * @date 2022/1/6
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonData handle(Exception e) {
        if (e instanceof BizException) {
            BizException bizException = (BizException) e;
            log.error("[业务异常]", bizException);
            return JsonData.buildError(bizException.getCode(), bizException.getMsg());
        } else {
            log.error("[系统异常]", e);
            return JsonData.buildError("系统异常，请联系开发人员");
        }
    }
}
