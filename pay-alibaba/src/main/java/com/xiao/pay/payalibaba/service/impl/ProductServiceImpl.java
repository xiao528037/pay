package com.xiao.pay.payalibaba.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.pay.payalibaba.dao.ProductMapper;
import com.xiao.pay.payalibaba.entity.Product;
import com.xiao.pay.payalibaba.service.ProductService;
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
