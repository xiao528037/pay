package com.xiao.pay.paywechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class PayWechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayWechatApplication.class, args);
    }

}
