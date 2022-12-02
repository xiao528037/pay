package com.xiao.pay.payalibaba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author aloneMan
 * @describe 支付宝支付程序启动类
 */
@SpringBootApplication
@EnableScheduling
public class PayAlibabaApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayAlibabaApplication.class, args);
    }
}
