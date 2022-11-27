package com.xiao.pay.paywechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.pay.paywechat.dao.ProductMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:51:03
 * @description
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 创建订单信息
     *
     * @param productId
     *         商品ID
     * @return 返回订单信息
     */
    @Transactional(rollbackFor = Exception.class)
    OrderInfo createOrder(Long productId);

    /**
     * 查询商品是否存在未支付的订单信息
     *
     * @param productId
     *         商品ID
     * @return 订单信息
     */
    @Transactional(readOnly = true)
    OrderInfo getNoPayOrderByproductId(Long productId);

    /**
     * 保存订单的支付二维码路径
     *
     * @param codeUrl
     *         二维码路径
     * @param orderNo
     *         订单编号
     */
    @Transactional(rollbackFor = Exception.class)
    void orderCodeUrl(String codeUrl, String orderNo);

    /**
     * 更新订单支付状态
     */
    void updateOrderInfoPayStatus();
}
