package com.xiao.pay.paywechat;

import com.xiao.pay.paywechat.config.WxPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;

@SpringBootTest
@Slf4j
class PayWechatApplicationTests {

    @Autowired
    WxPayConfig wxPayConfig;

    @Autowired
    CloseableHttpClient wxPayClient;
    @Test
    void contextLoads() {
//        PrivateKey privateKey =
//                wxPayConfig.getPrivateKey(wxPayConfig.getPrivateKeyPath());
//        log.info("{} ",privateKey);
        log.info("{} ",wxPayConfig);
    }

}
