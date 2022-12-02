package com.xiao.pay.payalibaba.config;

import com.alipay.api.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-12-01 10:29:37
 * @description
 */

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayClientConfig {

    private String appId;

    private String sellerId;

    private String gatewayUrl;

    private String merchantPrivateKey;

    private String alipayPublicKey;

    private String contentKey;

    private String returnUrl;

    private String notifyUrl;


    @Bean
    public AlipayClient alipayClient() {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(gatewayUrl);
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(merchantPrivateKey);
        alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
        alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);
        AlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient(alipayConfig);
        } catch (AlipayApiException e) {
            log.info("创建 alipayClient失败");
            throw new RuntimeException(e);
        }
        return alipayClient;
    }
}
