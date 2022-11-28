package com.xiao.pay.paywechat.contrller;

import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.entity.RefundInfo;
import com.xiao.pay.paywechat.service.OrderInfoService;
import com.xiao.pay.paywechat.service.WxPayService;
import com.xiao.pay.paywechat.util.HttpUtils;
import com.xiao.pay.paywechat.util.WechatPay2ValidatorForRequest;
import com.xiao.pay.paywechat.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-27 13:53:39
 * @description
 */

@CrossOrigin
@RestController
@Api(tags = "微信支付")
@Slf4j(topic = "WxPayController")
@RequestMapping("/api/wxpay")
public class WxPayController {


    private final WxPayService wxPayService;


    private final Verifier verifier;

    public WxPayController(WxPayService wxPayService, OrderInfoService orderInfoService, Verifier verifier) {
        this.wxPayService = wxPayService;
        this.verifier = verifier;
    }


    @PostMapping("/native/{productId}")
    @ApiOperation("订单生成")
    public Result nativePay(@PathVariable("productId") Long productId) throws IOException {
        Map map = wxPayService.nativePay(productId);
        return Result.success(map);
    }

    @PostMapping("/native/notify")
    @ApiOperation("接收微信支付通知")
    public String nativeNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        Map result = new HashMap(2);

        //处理通知信息参数
        String requestBody = HttpUtils.readData(request);

        HashMap<String, Object> bodyMap = gson.fromJson(requestBody, HashMap.class);
        String id = (String) bodyMap.get("id");
        log.info("支付通知的ID >>> {} ", id);
        log.info("支付通知信息 >>> {} ", bodyMap);

        //@TODO 验签
        WechatPay2ValidatorForRequest validator = new WechatPay2ValidatorForRequest(verifier, requestBody, id);
        boolean validate = validator.validate(request);
        if (!validate) {
            log.error("签名验证失败");
            result.put("code", 500);
            result.put("message", "fail");
        } else {
            log.info("{} ", "签名认证成功");
            result.put("code", 200);
            result.put("message", "success");
        }
        //@TODO 更新订单状态和保存支付日志
        wxPayService.processOrder(bodyMap);
        return gson.toJson(result);
    }

    @PostMapping("/refunds/notify")
    @ApiOperation("接收微信退款通知")
    public String nativeRefund(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        Map result = new HashMap(2);

        //处理通知信息参数
        String requestBody = HttpUtils.readData(request);

        HashMap<String, Object> bodyMap = gson.fromJson(requestBody, HashMap.class);
        String id = (String) bodyMap.get("id");
        log.info("支付通知的ID >>> {} ", id);
        log.info("支付通知信息 >>> {} ", bodyMap);

        //@TODO 验签
        WechatPay2ValidatorForRequest validator = new WechatPay2ValidatorForRequest(verifier, requestBody, id);
        boolean validate = validator.validate(request);
        if (!validate) {
            log.error("签名验证失败");
            result.put("code", 500);
            result.put("message", "fail");
        } else {
            log.info("{} ", "签名认证成功");
            result.put("code", 200);
            result.put("message", "success");
        }
        //@TODO 更新订单状态和保存支付日志
        wxPayService.processRefund(bodyMap);
        return gson.toJson(result);
    }

    /**
     * 取消订单
     *
     * @param orderNo
     *         订单标号
     * @return 返回取消状态
     * @throws IOException
     */
    @PostMapping("/cancel/{orderNo}")
    @ApiOperation("订单取消")
    public Result cancelOrder(@PathVariable("orderNo") String orderNo) throws IOException {
        wxPayService.cancelOrder(orderNo);
        return Result.success();
    }

    @GetMapping("/queryOrderInfo/{orderNo}")
    @ApiOperation("获取订单信息")
    public Result getOrderInfo(@PathVariable("orderNo") String orderNo) {
        String body = wxPayService.queryOrder(orderNo);
        Gson gson = new Gson();
        HashMap result = gson.fromJson(body, HashMap.class);
        return Result.success(result);
    }

    @ApiOperation("查询退款")
    @GetMapping("/query-refund/{refundNo}")
    public Result<Map> queryRefund(@PathVariable("refundNo") String refundNo) throws IOException {
        log.info("{} ", "查询退款");
        Map result = wxPayService.queryRefund(refundNo);
        return Result.success(result);
    }

    @PostMapping("/refund/{orderNo}/{reason}")
    @ApiOperation("退款")
    public Result refund(@PathVariable("orderNo") String orderNo, @PathVariable("reason") String reason) throws IOException {
        wxPayService.refund(orderNo, reason);
        return Result.success();
    }

    @ApiOperation("获取账单url：测试用")
    @GetMapping("/querybill/{billDate}/{type}")
    public Result queryTradeBill(
            @PathVariable String billDate,
            @PathVariable String type) throws Exception {
        log.info("获取账单url");
        String downloadUrl = wxPayService.queryBill(billDate, type);
        HashMap<String, String> result = new HashMap<>(1);
        result.put("downloadUrl", downloadUrl);
        return Result.success(result);
    }

    @ApiOperation("下载账单")
    @GetMapping("/downloadbill/{billDate}/{type}")
    public Result downloadBill(
            @PathVariable String billDate,
            @PathVariable String type) throws Exception {

        log.info("下载账单");
        String result = wxPayService.downloadBill(billDate, type);
        return Result.success(result);
    }
}
