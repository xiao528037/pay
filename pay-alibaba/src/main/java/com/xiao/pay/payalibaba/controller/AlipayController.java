package com.xiao.pay.payalibaba.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiao.pay.payalibaba.config.AlipayClientConfig;
import com.xiao.pay.payalibaba.entity.OrderInfo;
import com.xiao.pay.payalibaba.enums.OrderStatus;
import com.xiao.pay.payalibaba.service.AlipayService;
import com.xiao.pay.payalibaba.service.OrderInfoService;
import com.xiao.pay.payalibaba.service.PaymentInfoService;
import com.xiao.pay.payalibaba.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-12-01 11:38:11
 * @description
 */
@RestController
@Api(tags = "支付宝相关接口")
@RequestMapping("/alipay/trade")
@Slf4j(topic = "AlipayController")
public class AlipayController {

    private final AlipayService alipayService;

    private final AlipayClientConfig alipayClientConfig;

    private final OrderInfoService orderInfoService;

    private final PaymentInfoService paymentInfoService;

    public AlipayController(AlipayService alipayService, AlipayClientConfig alipayClientConfig, OrderInfoService orderInfoService, PaymentInfoService paymentInfoService) {
        this.alipayService = alipayService;
        this.alipayClientConfig = alipayClientConfig;
        this.orderInfoService = orderInfoService;
        this.paymentInfoService = paymentInfoService;
    }

    @ApiOperation("统一收单下单并支付页面接口的调用")
    @PostMapping("/page/pay/{productId}")
    public Result tradePagePay(@PathVariable("productId") Long productId) {
        log.info("{} ", "统一收单下单并支付页面接口的调用");
        String formStr = alipayService.tradeCreate(productId);
        return Result.success(formStr);
    }

    @ApiOperation("异步结果通知")
    @PostMapping("/notify")
    public String notify(@RequestParam Map<String, String> params) {
        log.info("收到支付宝的异步通知 >>> {}", params);
        String result = "failure";
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayClientConfig.getAlipayPublicKey(), AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2);
            if (!signVerified) {
                log.error("支付成功异步通知验签失败！ {} ");
            }
            log.info("异步通知验签成功 {}", "");
            //验证订单是否存在
            String outTradeNo = params.get("out_trade_no");
            QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(OrderInfo::getOrderNo, outTradeNo);
            OrderInfo orderInfo = orderInfoService.getOne(queryWrapper);
            Assert.notNull(orderInfo, "没有对应的订单信息");

            //验证订单金额是否一致
            String totalAmount = params.get("total_amount");
            int totalAmountInt = new BigDecimal(totalAmount).multiply(new BigDecimal(100)).intValue();
            int totalFeeInt = orderInfo.getTotalFee().intValue();
            Assert.isTrue(totalFeeInt == totalAmountInt, "金额不一致，请忽略本次通知");

            //验证商户号是否一致
            String sellerId = params.get("seller_id");
            Assert.isTrue(sellerId.equals(alipayClientConfig.getSellerId()), "商户号不一致，请忽略本次通知");

            //验证AppId是否一致
            String appId = params.get("app_id");
            Assert.isTrue(appId.equals(alipayClientConfig.getAppId()), "APPID不一致,请忽略本次通知");

            //判断是否支付成功
            String tradeStatus = params.get("trade_status");
            Assert.isTrue(tradeStatus.equals("TRADE_SUCCESS"), "支付未成功");

            //业务处理 修改订单状态 记录支付日志
            alipayService.processOrder(params);

            result = "success";
        } catch (AlipayApiException e) {
            log.info("异步通知处理失败 {}", result);
            return result;
        }

        return result;
    }

    @ApiOperation("取消订单")
    @PostMapping("/cancel/{orderNo}")
    public Result cancel(@PathVariable("orderNo") String orderNo) {
        log.info("取消订单 {}", "");
        alipayService.cancelOrder(orderNo);
        return Result.success();
    }

    @ApiOperation("查询订单")
    @PostMapping("/query/{orderNo}")
    public Result queryOrder(@PathVariable("orderNo") String orderNo) {
        log.info("查询订单 >>> {}", orderNo);
        String result = alipayService.queryOrder(orderNo);
        return Result.success(result);
    }

    @ApiOperation("订单退款")
    @PostMapping("/refund/{orderNo}/{reason}")
    public Result refund(@PathVariable("orderNo") String orderNo, @PathVariable("reason") String reason) {
        log.info("申请退款 {} {}", orderNo, reason);
        alipayService.refund(orderNo, reason);
        return Result.success();
    }

    @ApiOperation("退款查询")
    @PostMapping("/query/refund/{orderNo}")
    public Result queryRefund(@PathVariable("orderNo") String orderNo) {
        log.info("查询退款 {}", "");
        String result = alipayService.queryRefund(orderNo);
        return Result.success(result);
    }

    @ApiOperation("下载账单")
    @GetMapping("/bill/query/{billDate}/{type}")
    public Result queryBill(@PathVariable("billDate") String billDate, @PathVariable("type") String type) {
        log.info("获取账单URL {} {}", billDate, type);
        String downloadUrl = alipayService.queryBill(billDate, type);
        return Result.success(downloadUrl);
    }
}
