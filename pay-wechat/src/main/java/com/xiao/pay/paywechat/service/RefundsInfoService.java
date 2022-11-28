package com.xiao.pay.paywechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.pay.paywechat.entity.RefundInfo;

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
}
