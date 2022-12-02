package com.xiao.pay.payalibaba.controller;


import com.xiao.pay.payalibaba.entity.OrderInfo;
import com.xiao.pay.payalibaba.enums.OrderStatus;
import com.xiao.pay.payalibaba.service.OrderInfoService;
import com.xiao.pay.payalibaba.vo.Result;
import com.xiao.pay.payalibaba.vo.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-27 10:41:57
 * @description
 */
@Slf4j
@RestController
@Api(tags = "订单相关接口")
@RequestMapping("/api/v1/wechat/order")
@CrossOrigin
public class OrderController {

    private final OrderInfoService orderInfoService;



    public OrderController(OrderInfoService orderInfoService) {
        this.orderInfoService = orderInfoService;
    }

    @GetMapping("/list")
    @ApiOperation("获取订单信息")
    public Result<List> list() {
        List<OrderInfo> list = orderInfoService.list();
        return Result.success(list);
    }

    /**
     * 获取订单状态
     *
     * @param oderNo
     *         订单标号
     * @return 订单状态
     */
    @GetMapping("/queryOderStatus/{orderNo}")
    @ApiOperation("获取订单状态")
    public Result getOrderStatus(@PathVariable("orderNo") String oderNo) {
        String orderState = orderInfoService.getStateByOrderNo(oderNo);
        if (orderState == null) {
            return Result.fail();
        }
        if (orderState.equals(OrderStatus.SUCCESS.getType())) {
            return Result.success("支付成功");
        }
        return Result.instance(ResultCode.ORDER_STATUS, "订单正在支付");
    }


}
