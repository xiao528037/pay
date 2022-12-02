package com.xiao.pay.payalibaba;


import com.xiao.pay.payalibaba.config.AlipayClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
@Slf4j
class PayAlibabaApplicationTests {

    @Autowired
    private AlipayClientConfig alipayClientConfig;

    @Test
    void contextLoads() {
        log.info("{} ", alipayClientConfig);
    }

}
