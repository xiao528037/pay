package com.xiao.pay.paywechat.contrller;

import com.xiao.pay.paywechat.config.WxPayConfig;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.service.OrderInfoService;
import com.xiao.pay.paywechat.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-27 10:41:57
 * @description
 */
@Slf4j
@RestController
@Api(tags = "微信支付-订单相关接口")
@RequestMapping("/api/v1/wechat/order")
@CrossOrigin
public class OrderController {

    private final OrderInfoService orderInfoService;

    private final WxPayConfig wxPayConfig;

    public OrderController(OrderInfoService orderInfoService, WxPayConfig wxPayConfig) {
        this.orderInfoService = orderInfoService;
        this.wxPayConfig = wxPayConfig;
    }

    @GetMapping("/list")
    @ApiOperation("获取订单信息")
    public Result<List> list() {
        List<OrderInfo> list = orderInfoService.list();
        log.info("{} ", wxPayConfig);
        return Result.success(list);
    }
}
