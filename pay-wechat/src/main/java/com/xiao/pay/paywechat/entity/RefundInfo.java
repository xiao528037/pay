package com.xiao.pay.paywechat.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:46:18
 * @description 退款信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_refund_info")
public class RefundInfo implements Serializable {
    /**
     * 款单id
     */
    private Long id;

    /**
     * 商户订单编号
     */
    private String orderNo;

    /**
     * 商户退款单编号
     */
    private String refundNo;

    /**
     * 支付系统退款单号
     */
    private String refundId;

    /**
     * 原订单金额(分)
     */
    private Integer totalFee;

    /**
     * 退款金额(分)
     */
    private Integer refund;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 退款状态
     */
    private String refundStatus;

    /**
     * 申请退款返回参数
     */
    private String contentReturn;

    /**
     * 退款结果通知参数
     */
    private String contentNotify;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}