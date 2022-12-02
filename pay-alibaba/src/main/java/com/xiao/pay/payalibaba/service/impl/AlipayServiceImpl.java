package com.xiao.pay.payalibaba.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradePageRefundModel;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.xiao.pay.payalibaba.config.AlipayClientConfig;
import com.xiao.pay.payalibaba.entity.OrderInfo;
import com.xiao.pay.payalibaba.entity.RefundInfo;
import com.xiao.pay.payalibaba.enums.OrderStatus;
import com.xiao.pay.payalibaba.enums.alipay.AliPayTradeState;
import com.xiao.pay.payalibaba.service.AlipayService;
import com.xiao.pay.payalibaba.service.OrderInfoService;
import com.xiao.pay.payalibaba.service.PaymentInfoService;
import com.xiao.pay.payalibaba.service.RefundsInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-12-01 11:36:21
 * @description
 */
@Service("alipayService")
@Slf4j(topic = "AlipayServiceImpl")
public class AlipayServiceImpl implements AlipayService {


    private final OrderInfoService orderInfoService;

    private final AlipayClient alipayClient;

    private final PaymentInfoService paymentInfoService;

    private final RefundsInfoService refundsInfoService;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    private final AlipayClientConfig alipayClientConfig;

    public AlipayServiceImpl(OrderInfoService orderInfoService,
                             AlipayClient alipayClient,
                             PaymentInfoService paymentInfoService,
                             RefundsInfoService refundsInfoService,
                             AlipayClientConfig alipayClientConfig) {
        this.orderInfoService = orderInfoService;
        this.alipayClient = alipayClient;
        this.paymentInfoService = paymentInfoService;
        this.refundsInfoService = refundsInfoService;
        this.alipayClientConfig = alipayClientConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String tradeCreate(Long productId) {
        //生成订单
        OrderInfo orderInfo = orderInfoService.createOrder(productId);
        //调用支付宝接口
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //公共参数部分
        request.setNotifyUrl(alipayClientConfig.getNotifyUrl());
        request.setReturnUrl(alipayClientConfig.getReturnUrl());

        BigDecimal totalAmount = new BigDecimal(orderInfo.getTotalFee().toString()).divide(new BigDecimal(100));
        //组装当前业务方法的请求参数
        AlipayTradePagePayModel alipayTradePagePayModel = new AlipayTradePagePayModel();
        alipayTradePagePayModel.setOutTradeNo(orderInfo.getOrderNo());
        alipayTradePagePayModel.setTotalAmount(totalAmount.toString());
        alipayTradePagePayModel.setSubject(orderInfo.getTitle());
        alipayTradePagePayModel.setProductCode("FAST_INSTANT_TRADE_PAY");

        request.setBizModel(alipayTradePagePayModel);
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            String body = response.getBody();
            if (response.isSuccess()) {
                log.info("{} 结果 >>> {}", "支付宝接口调用成功", body);
            } else {
                log.error(">>> {} 结果 {}", "支付宝接口调用失败", body);
            }
            return body;
        } catch (AlipayApiException e) {
            log.error(">>> {} ", "支付宝接口调用发生异常");
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOrder(Map<String, String> params) {
        log.info("处理订单 {}", "");
        //更新订单状态
        if (reentrantLock.tryLock()) {
            try {
                String status = orderInfoService.getStateByOrderNo(params.get("out_trade_no"));
                if (status.equals(OrderStatus.NOTPAY.getType())) {
                    orderInfoService.updateStatusByOrderNo(params.get("out_trade_no"), OrderStatus.SUCCESS);
                    paymentInfoService.createPaymentInfoAlipay(params);
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {

        //调用支付宝提供的统一收单交易关闭接口
        this.closeOrder(orderNo);

        //更新用户订单状态
        orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
    }

    @Override
    public void closeOrder(String orderNo) {
        log.info("关单接口的调用，订单号 >>> {}", orderNo);
        try {
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            //组装当前业务方法的请求参数
            AlipayTradePagePayModel alipayTradePagePayModel = new AlipayTradePagePayModel();
            alipayTradePagePayModel.setOutTradeNo(orderNo);
            request.setBizModel(alipayTradePagePayModel);
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("订单取消成功 {}", response.getBody());
            } else {
                log.info("订单取消失败 {}", response.getBody());
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String queryOrder(String orderNo) {
        log.info("查询订单 >>> {}", orderNo);
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            //组装当前业务方法的请求参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(orderNo);
            request.setBizModel(model);
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("查询订单成功,返回结果 {}", response.getBody());
            } else {
                log.info("查询订单失败,返回结果 {}", response.getBody());
            }
            return response.getBody();
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkOrderStatus(String orderNo) {
        log.info("支付宝查单，核实订单是否支付 {}", orderNo);

        String result = this.queryOrder(orderNo);
        //订单未创建
        if (result == null) {
            log.warn("订单未创建 {}", orderNo);
            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
        } else {
            Gson gson = new Gson();
            HashMap<String, LinkedTreeMap> resultMap = gson.fromJson(result, HashMap.class);
            LinkedTreeMap alipayTradeQueryResponse = resultMap.get("alipay_trade_query_response");
            String tradeStatus = (String) alipayTradeQueryResponse.get("trade_status");
            if (AliPayTradeState.NOTPAY.getType().equals(tradeStatus)) {
                log.warn("订单未支付 {}", orderNo);
                //如果订单未支付，调用关单接口关闭订单
                this.closeOrder(orderNo);
                // 更新商户段订单状态
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
            } else if (AliPayTradeState.SUCCESS.getType().equals(tradeStatus)) {
                log.warn("订单已支付 {}", orderNo);
                // 更新商户段订单状态
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
                //记录支付日志
                paymentInfoService.createPaymentInfoAlipay(alipayTradeQueryResponse);
            }
        }
    }

    @Override
    public void refund(String orderNo, String reason) {
        log.info("调用退款API {} {}", orderNo, reason);
        try {
            RefundInfo refundInfo = refundsInfoService.createRefundByOrderNo(orderNo, reason);
            //调用统一收单交易退款接口
            AlipayTradeRefundApplyRequest request = new AlipayTradeRefundApplyRequest();
            //组装当前业务方法的请求参数
            AlipayTradePageRefundModel model = new AlipayTradePageRefundModel();
            model.setOutTradeNo(orderNo);
            BigDecimal refundAmount = new BigDecimal(refundInfo.getRefund().toString()).divide(new BigDecimal(100));
            model.setRefundAmount(refundAmount.toString());
            model.setRefundReason(reason);
            request.setBizModel(model);
            AlipayTradeRefundApplyResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("调用成功，返回结果 {}", response.getBody());
                //更新订单状态
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);

                //更新退款单
                refundsInfoService.updateRefundForAlipay(refundInfo.getRefundNo(), response.getBody(), AliPayTradeState.REFUND_SUCCESS.getType());
            } else {
                log.info("调用失败，返回结果 {}", response.getBody());
                //更新订单状态
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_ABNORMAL);

                //更新退款单
                refundsInfoService.updateRefundForAlipay(refundInfo.getRefundNo(), response.getBody(), AliPayTradeState.REFUND_ERROR.getType());
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String queryRefund(String orderNo) {
        try {
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            AlipayTradePageRefundModel model = new AlipayTradePageRefundModel();
            model.setOutTradeNo(orderNo);
            model.setOutRequestNo(orderNo);
            request.setBizModel(model);
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("调用成功,返回结果 {}", response.getBody());
                return response.getBody();
            } else {
                log.info("调用失败,返回码 {}", response.getBody());
                return null;
            }

        } catch (Exception e) {
            log.info("查询接口调用发生异常 {}", orderNo);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String queryBill(String billDate, String type) {
        log.info("获取账单下载URL {} {}", billDate, type);
        try {
            AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();

            AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
            model.setBillDate(billDate);
            model.setBillType(type);
            request.setBizModel(model);
            AlipayDataDataserviceBillDownloadurlQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info(" {}", "调用成功");
                String body = response.getBody();
                HashMap<String, LinkedTreeMap> resultMap = JSON.parseObject(body, HashMap.class);
                LinkedTreeMap dataDataserviceBillDownloadurlQueryResponse = resultMap.get("alipay_data_dataservice_bill_downloadurl_query_response");
                String billDownloadUrl = (String) dataDataserviceBillDownloadurlQueryResponse.get("bill_download_url");
                return billDownloadUrl;
            } else {
                log.info(" {}", "调用失败");
                throw new RuntimeException("账单调用失败");
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }
}
