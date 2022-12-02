package com.xiao.pay.payalibaba.task;

import com.xiao.pay.payalibaba.entity.OrderInfo;
import com.xiao.pay.payalibaba.enums.PayType;
import com.xiao.pay.payalibaba.service.AlipayService;
import com.xiao.pay.payalibaba.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-12-02 11:34:14
 * @description
 */
@Component
@Slf4j(topic = "AlipayTask")
public class AlipayTask {

    private final OrderInfoService orderInfoService;

    private final AlipayService alipayService;

    public AlipayTask(OrderInfoService orderInfoService,
                      AlipayService alipayService) {
        this.orderInfoService = orderInfoService;
        this.alipayService = alipayService;
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void orderConfirm() {
        log.info("查询五分钟未支付的订单 {}", "执行...");
        List<OrderInfo> noPayOrderByDuration = orderInfoService.getNoPayOrderByDuration(5, PayType.ALIPAY.getType());
        noPayOrderByDuration.forEach(orderInfo -> {
            //调用支付宝查单接口
            alipayService.checkOrderStatus(orderInfo.getOrderNo());
        });
    }
}
