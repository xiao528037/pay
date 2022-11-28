package com.xiao.pay.paywechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.pay.paywechat.dao.ProductMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.enums.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

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
     * 更新订单状态
     *
     * @param orderNo
     *         订单号
     * @param success
     *         订单状态
     */
    void updateStatusByOrderNo(String orderNo, OrderStatus success);

    /**
     * 获取订单状态
     *
     * @param outTradeNo
     *         订单编号
     * @return 状态
     */
    String getStateByOrderNo(String outTradeNo);


    /**
     * 查询超过指定时间未支付的订单
     * @param minutes 时间分钟
     * @return 未支付集合
     */
    List<OrderInfo> getNoPayOrderByDuration(int minutes);
}
