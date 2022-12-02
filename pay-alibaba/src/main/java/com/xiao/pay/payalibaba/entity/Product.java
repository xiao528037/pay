package com.xiao.pay.payalibaba.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:45:39
 * @description 产品表信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_product")
public class Product implements Serializable {
    /**
     * 商Bid
     */
    private Long id;

    /**
     * 商品名称
     */
    private String title;

    /**
     * 价格(分)
     */
    private Integer price;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}