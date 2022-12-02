package com.xiao.pay.payalibaba.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.xiao.pay.payalibaba.dao.PaymentInfoMapper;
import com.xiao.pay.payalibaba.entity.PaymentInfo;
import com.xiao.pay.payalibaba.enums.PayType;
import com.xiao.pay.payalibaba.service.PaymentInfoService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:55:29
 * @description
 */
@Service("paymentInfoService")
@Slf4j(topic = "PaymentInfoServiceImpl")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    private final PaymentInfoMapper paymentInfoMapper;

    public PaymentInfoServiceImpl(PaymentInfoMapper paymentInfoMapper) {
        this.paymentInfoMapper = paymentInfoMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPaymentInfo(String plainText) {
        log.info(">>> {} ", "记录支付日志");
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        //订单号
        String orderNo = (String) plainTextMap.get("out_trade_no");
        //微信支付订单号
        String transactionId = (String) plainTextMap.get("transaction_id");
        //交易类型
        String tradeType = (String) plainTextMap.get("trade_type");
        //交易状态
        String tradeState = (String) plainTextMap.get("trade_state");
        //支付金额
        LinkedTreeMap<String, Object> amountMap = (LinkedTreeMap<String, Object>) plainTextMap.get("amount");
        Double payerTotal = (Double) amountMap.get("payer_total");

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .orderNo(orderNo)
                .paymentType(PayType.WXPAY.getType())
                .transactionId(transactionId)
                .tradeType(tradeType)
                .tradeState(tradeState)
                .payerTotal(payerTotal.intValue())
                .content(plainText)
                .build();
        int insert = paymentInfoMapper.insert(paymentInfo);
        Assert.isTrue(insert > 0, "支付日志保存失败");
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean paymentINfoIsExist(String outTradeNo) {
        log.info(">>> {} ", "查询订单日志");
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PaymentInfo::getOrderNo, outTradeNo);
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(queryWrapper);
        if (paymentInfo == null) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPaymentInfoAlipay(Map<String, String> params) {
        //获取订单号
        String outTradeNo = params.get("out_trade_no");
        //获取支付宝业务编号
        String tradeNo = params.get("trade_no");
        //交易状态
        String tradeStatus = params.get("trade_status");
        //支付金额
        String totalAmount = params.get("total_amount");
        int totalAmountInt = new BigDecimal(totalAmount).divide(new BigDecimal(100)).intValue();
        String contentJson = JSON.toJSONString(params);
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .orderNo(outTradeNo)
                .paymentType(PayType.ALIPAY.getType())
                .transactionId(tradeNo)
                .tradeType("电脑网站支付")
                .tradeState(tradeStatus)
                .payerTotal(totalAmountInt)
                .content(contentJson)
                .build();
        paymentInfoMapper.insert(paymentInfo);
    }
}
