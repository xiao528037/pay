package com.xiao.pay.paywechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.pay.paywechat.dao.PaymentInfoMapper;
import com.xiao.pay.paywechat.entity.PaymentInfo;
import com.xiao.pay.paywechat.service.PaymentInfoService;
import org.springframework.stereotype.Service;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:55:29
 * @description
 */
@Service("paymentInfoService")
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
}
