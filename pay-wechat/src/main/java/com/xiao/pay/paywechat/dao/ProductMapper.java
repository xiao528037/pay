package com.xiao.pay.paywechat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.pay.paywechat.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:48:57
 * @description
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
