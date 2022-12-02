package com.xiao.pay.payalibaba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.pay.payalibaba.entity.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:48:36
 * @description
 */
@Mapper
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {

    /**
     * 记录订单日志
     * @param plainText 订单信息
     */
    void createPaymentInfo(String plainText);
}
