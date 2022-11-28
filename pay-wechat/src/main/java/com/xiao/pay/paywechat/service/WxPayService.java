package com.xiao.pay.paywechat.service;

import com.xiao.pay.paywechat.entity.OrderInfo;

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
    public Map nativePay(Long productId) throws IOException;

    /**
     * 解密订单数据和更新订单状态
     *
     * @param bodyMap
     */
    void processOrder(HashMap<String, Object> bodyMap);

    /**
     * 取消订单
     *
     * @param orderNo
     *         订单编号
     * @throws IOException
     */
    public void cancelOrder(String orderNo) throws IOException;

    /**
     * 获取订单信息
     *
     * @param orderNo
     *         订单编号
     * @return 订单信息
     */
    String queryOrder(String orderNo);

    /**
     * 更具订单号查询微信支付查单接口，合适订单状态
     * 如果支付，更新商户端订单状态
     * 如果未支付，则调用关闭接口关闭订单，并更新商户端订单状态
     *
     * @param orderNo
     *         订单编号
     */
    void checkOrderStatus(String orderNo);

    /**
     * 退款
     *
     * @param orderNO
     *         订单编号
     * @param reason
     *         退款信息
     * @throws IOException
     */
    void refund(String orderNO, String reason) throws IOException;

    /**
     * 更具退款订单号查询退款订单信息
     *
     * @param refundNo
     *         退款订单号
     * @return 退款信息
     */
    Map queryRefund(String refundNo) throws IOException;

    /**
     * 微信退款通知
     *
     * @param bodyMap
     *         微信平台发送过来的信息
     */
    void processRefund(HashMap<String, Object> bodyMap);

    String queryBill(String billDate, String type) throws Exception;

    String downloadBill(String billDate, String type) throws Exception;
}
