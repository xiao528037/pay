package com.xiao.pay.paywechat.contrller;

import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
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
    public Result nativePay(@PathVariable("productId") Long productId) {
        Map map = wxPayService.nativePay(productId);
        return Result.success(map);
    }

    @PostMapping("/native/notify")
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
}
