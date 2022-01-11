package com.bhnote.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bhnote.enums.BizCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Bingo
 * @date 2021/9/30 9:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonData {

    /**
     * 状态码 0表示成功，非0表示业务异常
     */
    private Integer code;

    /**
     * 数据
     */
    private Object data;

    /**
     * 描述
     */
    private String msg;

    /**
     * 获取远程调用数据
     */
    public <T> T getData(TypeReference<T> typeReference) {
        return JSON.parseObject(JSON.toJSONString(data), typeReference);
    }

    /**
     * 成功，不传入数据
     */
    public static JsonData buildSuccess() {
        return new JsonData(0, null, null);
    }

    /**
     * 成功，传入数据
     */
    public static JsonData buildSuccess(Object data) {
        return new JsonData(0, data, null);
    }

    /**
     * 失败，传入错误信息
     */
    public static JsonData buildError(String msg) {
        return new JsonData(-1, null, msg);
    }

    /**
     * 失败，传入状态码和错误信息
     */
    public static JsonData buildError(int code, String msg) {
        return new JsonData(code, null, msg);
    }

    /**
     * 失败，传入枚举
     */
    public static JsonData buildError(BizCodeEnum codeEnum) {
        return JsonData.buildError(codeEnum.getCode(), codeEnum.getMessage());
    }
}