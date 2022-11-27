package com.xiao.pay.paywechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.pay.paywechat.dao.OrderInfoMapper;
import com.xiao.pay.paywechat.dao.ProductMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.entity.Product;
import com.xiao.pay.paywechat.enums.OrderStatus;
import com.xiao.pay.paywechat.service.OrderInfoService;
import com.xiao.pay.paywechat.util.OrderNoUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:54:31
 * @description
 */
@Service("orderInfoService")
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    private final ProductMapper productMapper;
    private final OrderInfoMapper orderInfoMapper;

    public OrderInfoServiceImpl(ProductMapper productMapper, OrderInfoMapper orderInfoMapper) {
        this.productMapper = productMapper;
        this.orderInfoMapper = orderInfoMapper;
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
        //单位：分
        orderInfo.setTotalFee(product.getPrice());
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
}
