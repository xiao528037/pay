package com.xiao.pay.paywechat.task;

import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.entity.RefundInfo;
import com.xiao.pay.paywechat.service.OrderInfoService;
import com.xiao.pay.paywechat.service.RefundsInfoService;
import com.xiao.pay.paywechat.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-28 16:25:49
 * @description
 */
@Component
@Slf4j
public class WxPayTask {

    private final OrderInfoService orderInfoService;

    private final WxPayService wxPayService;

    private final RefundsInfoService refundsInfoService;

    public WxPayTask(
            OrderInfoService orderInfoService,
            WxPayService wxPayService,
            RefundsInfoService refundsInfoService) {
        this.orderInfoService = orderInfoService;
        this.wxPayService = wxPayService;
        this.refundsInfoService = refundsInfoService;
    }

    /**
     * 每隔三十秒查询一次，超过五分钟未支付的订单
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void wxPayNoPayOrder() {
        List<OrderInfo> noPyaList = orderInfoService.getNoPayOrderByDuration(5);
        for (OrderInfo orderInfo : noPyaList) {
            log.info("<<<<<< {} ", orderInfo);
            //检查支付订单状态
            wxPayService.checkOrderStatus(orderInfo.getOrderNo());
        }
    }

    /**
     * 每隔三十秒查询一次，超过五分钟未退款的订单
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void wxPayRefund() {
        List<RefundInfo> noRefundOrderByDuration = refundsInfoService.getNoRefundOrderByDuration(5);
        for (RefundInfo refundInfo : noRefundOrderByDuration) {
            log.info("<<<<<< {} ",refundInfo);
            //检查支付订单状态
        }
    }
}
