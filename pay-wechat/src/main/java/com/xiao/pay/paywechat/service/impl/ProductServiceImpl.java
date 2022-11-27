package com.xiao.pay.paywechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.pay.paywechat.dao.ProductMapper;
import com.xiao.pay.paywechat.entity.Product;
import com.xiao.pay.paywechat.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 10:55:36
 * @description
 */
@Service("productService")
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
}
