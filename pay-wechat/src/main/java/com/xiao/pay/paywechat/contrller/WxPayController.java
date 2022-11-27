package com.xiao.pay.paywechat.contrller;

import com.xiao.pay.paywechat.service.WxPayService;
import com.xiao.pay.paywechat.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-27 13:53:39
 * @description
 */

@RestController
@RequestMapping("/api/v1/wxpay")
@Api(tags = "微信支付")
public class WxPayController {


    private final WxPayService wxPayService;

    public WxPayController(WxPayService wxPayService) {
        this.wxPayService = wxPayService;
    }


    @PostMapping("/native/{productId}")
    @ApiOperation("订单生成")
    public Result nativePay(@PathVariable("productId") Long productId) {
        Map map = wxPayService.nativePay(productId);
        return Result.success(map);
    }

}
