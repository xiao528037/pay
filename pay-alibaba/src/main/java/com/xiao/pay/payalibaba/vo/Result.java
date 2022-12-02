package com.xiao.pay.payalibaba.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-25 18:01:21
 * @description
 */
@Data
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static Result success() {
        return new Result(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    public static <T> Result fail(T data) {
        return new Result(ResultCode.FAIL.getCode(), ResultCode.FAIL.getMessage(), data);
    }

    public static Result fail() {
        return new Result(ResultCode.FAIL.getCode(), ResultCode.FAIL.getMessage());
    }

    public static <T> Result<T> instance(ResultCode code, T data) {
        Result<T> tResult = new Result<>();
        tResult.setCode(code.getCode());
        tResult.setMessage(code.getMessage());
        tResult.setData(data);
        return tResult;
    }

}
