package com.xiao.pay.paywechat.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:07:21
 * @description
 */


@AllArgsConstructor
public enum ResultCode {

    /**
     * 状态码
     */
    SUCCESS(1, "处理成功"),
    FAIL(2, "处理失败"),

    ORDER_STATUS(101, "支付状态");

    private Integer code;

    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

