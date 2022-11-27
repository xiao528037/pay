package com.xiao.pay.paywechat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:48:04
 * @description
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 查询指定商品没有支付的订单信息
     *
     * @param productId
     *         商品信息
     * @param type
     *         状态
     * @return 订单信息
     */
    @Select("SELECT * FROM payment_demo.t_order_info WHERE product_id=#{productID,jdbcType=BIGINT} AND order_status=#{type,jdbcType=VARCHAR} LIMIT 1")
    OrderInfo getNoPayOrderByproductId(@Param("productID") Long productId, @Param("type") String type);

    /**
     * 保存订单二维码地址
     *
     * @param codeUrl
     *         支付二维码Url
     * @param orderNo
     *         订单编号
     * @return 更新数据条数
     */
    @Update("UPDATE payment_demo.t_order_info SET code_url = #{codeUrl,jdbcType=VARCHAR} WHERE order_no=#{orderNo,jdbcType=VARCHAR}")
    int saveCodeUrlByOrderNo(@Param("codeUrl") String codeUrl, @Param("orderNo") String orderNo);

    /**
     * 更新订单状态
     *
     * @param orderNo
     *         订单标号
     * @param status
     *         订单状态
     * @return 更新条数
     */
    @Update("UPDATE payment_demo.t_order_info SET order_status= #{status,jdbcType=VARCHAR} WHERE order_no=#{orderNo,jdbcType=VARCHAR} ")
    int updateStatusByOrderNo(@Param("orderNo") String orderNo, @Param("status") String status);

    /**
     * 查询订单状态
     *
     * @param orderNo
     *         订单编号
     * @return 订单状态
     */
    @Select("SELECT order_status FROM payment_demo.t_order_info WHERE order_no=#{orderNo,jdbcType=VARCHAR}")
    String getStateByOrderNo(@Param("orderNo") String orderNo);
}
