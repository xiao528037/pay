package com.xiao.pay.paywechat.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-27 13:56:28
 * @description
 */
public interface WxPayService {

    /**
     * 本地支付
     *
     * @param productId
     * @return 响应信息
     * @throws IOException
     */
    public Map nativePay(Long productId);

    /**
     * 解密订单数据和更新订单状态
     *
     * @param bodyMap
     */
    void processOrder(HashMap<String, Object> bodyMap);
}
