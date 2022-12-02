package com.xiao.pay.payalibaba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.pay.payalibaba.entity.RefundInfo;


import java.util.List;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:54:00
 * @description
 */
public interface RefundsInfoService extends IService<RefundInfo> {

    /**
     * 创建退款订单
     *
     * @param orderNO
     *         订单编号
     * @param reason
     *         退款细腻些
     * @return 退款信息
     */
    RefundInfo createRefundByOrderNo(String orderNO, String reason);

    /**
     * 更新退款订单信息
     *
     * @param content
     */
    void updateRefund(String content);

    /**
     * 找出申请退款超过指定时间，并且未成功的退款单
     *
     * @param minutes
     *         分钟
     * @return 退款订单列表
     */
    List<RefundInfo> getNoRefundOrderByDuration(int minutes);

    /**
     * 更新
     * @param refundNo 退款订单号
     * @param body 支付宝返回的状态
     * @param type 状态
     */
    void updateRefundForAlipay(String refundNo, String body, String type);
}
