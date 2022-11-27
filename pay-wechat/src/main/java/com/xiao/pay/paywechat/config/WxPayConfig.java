package com.xiao.pay.paywechat.config;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Arrays;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-27 11:01:05
 * @description 微信支付相关配置文件
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wxpay")
@PropertySource("classpath:wxpay.properties")
public class WxPayConfig {

    /**
     * 商户号
     */
    private String mchid;

    /**
     * 商户API证书序列号
     */
    private String mchSerialNo;

    /**
     * # 商户私钥文件
     */
    private String privateKeyPath;

    /**
     * APIv3密钥
     */
    private String apiV3Key;

    /**
     * APPID
     */
    private String appid;

    /**
     * 微信服务器地址
     */
    private String domian;

    /**
     * 接收结果通知地址
     */
    private String notifyDomain;

    /**
     * APIv2密钥
     */
    private String partnerKey;

    /**
     * 获取商户私钥
     *
     * @param privateKeyPath
     * @return 返回私钥
     */
    private PrivateKey getPrivateKey(String filename) {
        PrivateKey privateKey;
        try {
            //获取classpath目录下的文件
            ClassPathResource classPathResource = new ClassPathResource(filename);
            privateKey = PemUtil.loadPrivateKey(classPathResource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("商户私钥文件不错在");
        }
        return privateKey;
    }


    /**
     * 获取平台证书
     */
    @Bean
    public Verifier verifier() {
        CertificatesManager certificatesManger = certificatesManger();
        Verifier verifier;
        // 从证书管理器中获取verifier
        try {
            verifier = certificatesManger.getVerifier((this.getMchid()));
        } catch (NotFoundException e) {
            throw new RuntimeException("获取微信平台证书失败");
        }
        return verifier;
    }

    /**
     * 获取政府管理器实例
     *
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws HttpCodeException
     */
    @Bean
    public CertificatesManager certificatesManger() {
        CertificatesManager certificatesManager;
        try {
            // 获取证书管理器实例
            certificatesManager = CertificatesManager.getInstance();
            // 向证书管理器增加需要自动更新平台证书的商户信息
            certificatesManager.putMerchant(mchid, new WechatPay2Credentials(mchid,
                    new PrivateKeySigner(mchSerialNo, getPrivateKey(privateKeyPath))), apiV3Key.getBytes(StandardCharsets.UTF_8));
            // ... 若有多个商户号，可继续调用putMerchant添加商户信息
        } catch (Exception e) {
            throw new RuntimeException("获取证书管理器失败");
        }
        return certificatesManager;
    }

    /**
     * 获取微信客户端链接
     *
     * @return 微信客户端
     */
    @Bean
    public CloseableHttpClient wxPayClient() {
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(this.mchid, this.mchSerialNo, getPrivateKey(this.privateKeyPath))
                .withWechatPay(Arrays.asList(verifier().getValidCertificate()));
        // ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient

        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
        CloseableHttpClient httpClient = builder.build();
        return httpClient;
    }
}
