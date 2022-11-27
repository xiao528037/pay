package com.xiao.pay.paywechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.pay.paywechat.entity.PaymentInfo;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:53:19
 * @description
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    /**
     * 保存支付信息日志
     *
     * @param plainText
     *         日志信息
     */
    void createPaymentInfo(String plainText);

    /**
     * 判断支付日志是否已经存在
     *
     * @param outTradeNo
     * @return 返回是否存在
     */
    Boolean paymentINfoIsExist(String outTradeNo);
}
