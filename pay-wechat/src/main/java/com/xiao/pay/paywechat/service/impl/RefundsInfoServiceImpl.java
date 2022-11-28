package com.xiao.pay.paywechat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.xiao.pay.paywechat.dao.RefundInfoMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.entity.RefundInfo;
import com.xiao.pay.paywechat.enums.wxpay.WxRefundStatus;
import com.xiao.pay.paywechat.service.OrderInfoService;
import com.xiao.pay.paywechat.service.RefundsInfoService;
import com.xiao.pay.paywechat.util.OrderNoUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:55:42
 * @description
 */
@Service("refundInfoService")
public class RefundsInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundsInfoService {

    private final OrderInfoService orderInfoService;

    public RefundsInfoServiceImpl(OrderInfoService orderInfoService) {
        this.orderInfoService = orderInfoService;
    }

    @Override
    public RefundInfo createRefundByOrderNo(String orderNo, String reason) {
        //更具订单号获取订单信息
        QueryWrapper<OrderInfo> orderInfoQuery = new QueryWrapper<>();
        orderInfoQuery.lambda().eq(OrderInfo::getOrderNo, orderNo);
        OrderInfo orderInfo = orderInfoService.getOne(orderInfoQuery);

        //根据订单号生成退款订单
        RefundInfo refundInfo = RefundInfo.builder()
                //订单编号
                .orderNo(orderNo)
                //退款单编号
                .refundNo(OrderNoUtils.getRefundNo())
                //原订单金额（分）
                .totalFee(orderInfo.getTotalFee())
                //退款金额（分）
                .refund(orderInfo.getTotalFee())
                //退款原因
                .reason(reason)
                .build();
        //保存退款订单
        baseMapper.insert(refundInfo);
        return refundInfo;
    }

    @Override
    public void updateRefund(String content) {
        //将JSON字符串转换成Map
        Gson gson = new Gson();
        HashMap<String, String> resultMap = gson.fromJson(content, HashMap.class);

        //根据退款单表好修改退款单
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("refund_no", resultMap.get("out_refund_no"));

        //设置要修改的字段
        RefundInfo.RefundInfoBuilder refundInfoBuilder = RefundInfo.builder()
                //微信支付退款单号
                .refundId(resultMap.get("refund_id"));

        //查询退款和申请退款中的返回参数
        if (resultMap.get("status") != null) {
            //退款状态
            refundInfoBuilder.refundStatus(resultMap.get("status"));
            //将全部响应结果存入数据库的content字段
            refundInfoBuilder.contentReturn(content);
        }

        //退款回调中的回调参数
        if (resultMap.get("refund_status") != null) {
            refundInfoBuilder.refundStatus(resultMap.get("status"));
            refundInfoBuilder.contentNotify(content);
        }
        RefundInfo refundInfo = refundInfoBuilder.build();
        //更新退款单
        baseMapper.update(refundInfo, queryWrapper);
    }

    @Override
    public List<RefundInfo> getNoRefundOrderByDuration(int minutes) {
        Instant minus = Instant.now().minus(Duration.ofMinutes(minutes));
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(RefundInfo::getRefundStatus, WxRefundStatus.PROCESSING.getType())
                .le(RefundInfo::getCreateTime, minus);
        List<RefundInfo> refundInfoList = baseMapper.selectList(queryWrapper);
        return refundInfoList;
    }
}
