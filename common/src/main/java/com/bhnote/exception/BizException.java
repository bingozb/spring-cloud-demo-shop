package com.bhnote.exception;

import com.bhnote.enums.BizCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常
 *
 * @author bingo
 * @date 2022/1/6
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BizException extends RuntimeException {

    private Integer code;
    private String msg;

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }

    public BizException(BizCodeEnum bizCodeEnum) {
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMessage();
    }
}