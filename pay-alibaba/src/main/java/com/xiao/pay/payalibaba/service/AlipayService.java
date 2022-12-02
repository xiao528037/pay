package com.xiao.pay.payalibaba.service;

import java.util.Map;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-12-01 11:35:58
 * @description
 */
public interface AlipayService {
    String tradeCreate(Long productId);

    /**
     * 修改订单状态 记录支付日志
     *
     * @param params
     */
    void processOrder(Map<String, String> params);

    /**
     * 取消订单
     *
     * @param orderNo
     *         订单编号
     */
    void cancelOrder(String orderNo);

    /**
     * 调用支付宝接口取消订单
     *
     * @param orderNo
     *         订单编号
     */
    void closeOrder(String orderNo);

    /**
     * 查询订单信息
     *
     * @param orderNo
     *         订单号
     * @return 订单信息
     */
    String queryOrder(String orderNo);

    /**
     * 支付宝查单
     *
     * @param orderNo
     */
    void checkOrderStatus(String orderNo);

    /**
     * 退款单生成
     *
     * @param orderNo
     *         订单号
     * @param reason
     *         退款原因
     */
    void refund(String orderNo, String reason);

    /**
     * 查询退款信息
     *
     * @param orderNo
     *         订单编号
     * @return 退款信息
     */
    String queryRefund(String orderNo);

    /**
     * 获取下载账单URl
     * @param billDate 日期
     * @param type 类型
     * @return 下载URL
     */
    String queryBill(String billDate, String type);
}
