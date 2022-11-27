package com.xiao.pay.paywechat.contrller;

import com.xiao.pay.paywechat.entity.Product;
import com.xiao.pay.paywechat.service.ProductService;
import com.xiao.pay.paywechat.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-25 18:19:03
 * @description
 */
@RestController
@CrossOrigin
@RequestMapping("/pay/wechat")
@Api(tags = "微信支付相关接口")
@Slf4j(topic = "PayController")
public class PayController {


    private final ProductService productService;

    public PayController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/getProductList")
    @ApiOperation("获取所有商品列表")
    public Result getProductList() {
        List<Product> list = productService.list();
        return Result.success(list);
    }

}
