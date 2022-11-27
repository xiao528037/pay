package com.xiao.pay.paywechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.pay.paywechat.dao.RefundInfoMapper;
import com.xiao.pay.paywechat.entity.RefundInfo;
import com.xiao.pay.paywechat.service.RefundInfoService;
import org.springframework.stereotype.Service;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:55:42
 * @description
 */
@Service("refundInfoService")
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
}
