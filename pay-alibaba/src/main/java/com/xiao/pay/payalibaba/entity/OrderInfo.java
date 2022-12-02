package com.xiao.pay.payalibaba.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:44:17
 * @description 订单表信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_order_info")
public class OrderInfo implements Serializable {
    /**
     * 订单id
     */
    private Long id;

    /**
     * 订单标题
     */
    private String title;

    /**
     * 商户订单编号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 支付产品id
     */
    private Long productId;

    /**
     * 订单金额(分)
     */
    private Integer totalFee;

    /**
     * 订单二维码连接
     */
    private String codeUrl;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 支付类型
     */
    private String paymentType;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}