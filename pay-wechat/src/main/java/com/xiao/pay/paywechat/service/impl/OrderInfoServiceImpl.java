package com.xiao.pay.paywechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.pay.paywechat.dao.OderInfoMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.service.OrderInfoService;
import org.springframework.stereotype.Service;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:54:31
 * @description
 */
@Service("orderInfoService")
public class OrderInfoServiceImpl extends ServiceImpl<OderInfoMapper, OrderInfo> implements OrderInfoService {
}
