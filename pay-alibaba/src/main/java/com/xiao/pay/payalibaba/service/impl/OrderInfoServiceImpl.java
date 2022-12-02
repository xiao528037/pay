package com.xiao.pay.payalibaba.service.impl;

import com.alipay.api.AlipayClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.pay.payalibaba.dao.OrderInfoMapper;
import com.xiao.pay.payalibaba.dao.ProductMapper;
import com.xiao.pay.payalibaba.entity.OrderInfo;
import com.xiao.pay.payalibaba.entity.Product;
import com.xiao.pay.payalibaba.enums.OrderStatus;
import com.xiao.pay.payalibaba.enums.PayType;
import com.xiao.pay.payalibaba.service.OrderInfoService;

import com.xiao.pay.payalibaba.util.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:54:31
 * @description
 */
@Service("orderInfoService")
@Slf4j(topic = "OrderInfoServiceImpl")
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    private final ProductMapper productMapper;
    private final OrderInfoMapper orderInfoMapper;

    private final AlipayClient alipayClient;

    public OrderInfoServiceImpl(ProductMapper productMapper, OrderInfoMapper orderInfoMapper, AlipayClient alipayClient) {
        this.productMapper = productMapper;
        this.orderInfoMapper = orderInfoMapper;
        this.alipayClient = alipayClient;
    }

    @Override
    public OrderInfo createOrder(Long productId) {
        //是否有商品未支付
        OrderInfo noPayOrderByproductId = this.getNoPayOrderByproductId(productId);
        if (noPayOrderByproductId != null) {
            return noPayOrderByproductId;
        }
        //获取商品信息
        Product product = productMapper.selectById(productId);
        Assert.notNull(product, "没有查询到相关商品信息");
        //生成订单信息
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTitle("购买" + product.getTitle());
        orderInfo.setOrderNo(OrderNoUtils.getOrderNo());
        orderInfo.setProductId(product.getId());
        orderInfo.setPaymentType(PayType.ALIPAY.getType());
        //单位：分
        orderInfo.setTotalFee(product.getPrice());
        //未支付
        orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());
        int insert = orderInfoMapper.insert(orderInfo);
        Assert.isTrue(insert > 0, "添加订单信息失败");
        return orderInfo;
    }

    @Override
    public OrderInfo getNoPayOrderByproductId(Long productId) {
        return orderInfoMapper.getNoPayOrderByproductId(productId, OrderStatus.NOTPAY.getType());
    }

    @Override
    public void orderCodeUrl(String codeUrl, String orderNo) {
        int i = orderInfoMapper.saveCodeUrlByOrderNo(codeUrl, orderNo);
        Assert.isTrue(i > 0, "更新订单支付二维码信息失败");
    }

    @Override
    public void updateStatusByOrderNo(String orderNo, OrderStatus success) {
        int i = orderInfoMapper.updateStatusByOrderNo(orderNo, success.getType());
        Assert.isTrue(i > 0, "更新订单状态失败");
    }

    @Override
    public String getStateByOrderNo(String outTradeNo) {
        return orderInfoMapper.getStateByOrderNo(outTradeNo);
    }

    @Override
    public List<OrderInfo> getNoPayOrderByDuration(int minutes, String paymentType) {
        Instant minus = Instant.now().minus(Duration.ofMinutes(minutes));
        QueryWrapper<OrderInfo> query = new QueryWrapper<>();
        query.lambda()
                .eq(OrderInfo::getOrderStatus, OrderStatus.NOTPAY.getType())
                .eq(OrderInfo::getPaymentType, paymentType)
                .le(OrderInfo::getCreateTime, minus);
        List<OrderInfo> orderInfos = orderInfoMapper.selectList(query);

        return orderInfos;
    }


}
