package com.xiao.pay.paywechat.service.impl;

import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.xiao.pay.paywechat.config.WxPayConfig;
import com.xiao.pay.paywechat.dao.PaymentInfoMapper;
import com.xiao.pay.paywechat.dao.ProductMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.enums.OrderStatus;
import com.xiao.pay.paywechat.enums.wxpay.WxApiType;
import com.xiao.pay.paywechat.enums.wxpay.WxNotifyType;
import com.xiao.pay.paywechat.service.OrderInfoService;
import com.xiao.pay.paywechat.service.PaymentInfoService;
import com.xiao.pay.paywechat.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-27 13:57:09
 * @description
 */
@Service("wxPayService")
@Slf4j(topic = "WxPayServiceImpl")
public class WxPayServiceImpl implements WxPayService {

    private final ProductMapper productMapper;

    private final OrderInfoService orderInfoService;

    private final WxPayConfig wxPayConfig;

    private final CloseableHttpClient wxPayClient;

    private final PaymentInfoService paymentInfoService;


    public WxPayServiceImpl(ProductMapper productMapper,
                            OrderInfoService orderInfoService,
                            WxPayConfig wxPayConfig,
                            CloseableHttpClient wxPayClient,
                            PaymentInfoService paymentInfoService) {
        this.productMapper = productMapper;
        this.orderInfoService = orderInfoService;
        this.wxPayConfig = wxPayConfig;
        this.wxPayClient = wxPayClient;
        this.paymentInfoService = paymentInfoService;
    }

    @Override
    public Map nativePay(Long productId) {
        //生成订单
        OrderInfo order = orderInfoService.createOrder(productId);
        //判断是否存在code url如果存在是未支付的订单信息，如果没有是新生成的订单信息
        String codeUrl;
        if ((codeUrl = order.getCodeUrl()) != null && !codeUrl.isEmpty()) {
            return getResultMap(codeUrl, order.getOrderNo());
        }
        log.info(">>>>>>>>>>> {} ", "调用微信支付平台接口，返回支付二维码");
        String concat = wxPayConfig.getDomian().concat(WxApiType.NATIVE_PAY.getType());
        HttpPost httpPost = new HttpPost(concat);
        //生成请求参数
        String requestBody = createRequestBody(order);
        StringEntity stringEntity = new StringEntity(requestBody, "utf-8");
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Accept", "application/json");

        CloseableHttpResponse response = null;
        String body;
        try {
            response = wxPayClient.execute(httpPost);
            //获取响应体
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            log.info(">>>>>>> {} ", "下单失败");
            throw new RuntimeException(e);
        }

        //响应状态
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            log.info("下单成功,响应体数据 >>> {} ", body);
        } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
            log.info("{} ", "请求成功");
        } else {
            log.info("Native 下单失败,状态码 >>> {} ", statusCode);
            Assert.isTrue(false, "请求微信支付下单失败");
        }
        //解析响应体
        return analysisBody(body, order);
    }

    private Map analysisBody(String body, OrderInfo orderInfo) {
        Gson gson = new Gson();
        HashMap wechatResult = gson.fromJson(body, HashMap.class);
        String codeUrl = (String) wechatResult.get("code_url");
        String orderNo;
        orderInfoService.orderCodeUrl(codeUrl, orderNo = orderInfo.getOrderNo());
        return getResultMap(codeUrl, orderNo);
    }

    @NotNull
    private static Map getResultMap(String codeUrl, String orderNo) {
        Map result = new HashMap(2);
        result.put("codeUrl", codeUrl);
        result.put("orderNo", orderNo);
        return result;
    }

    /**
     * 微信下单接口请求参数
     *
     * @param orderInfo
     * @return
     */
    public String createRequestBody(OrderInfo orderInfo) {
        // 请求body参数
        Gson gson = new Gson();
        Map paramsMap = new HashMap();
        // 公共号，小程序等appid
        paramsMap.put("appid", wxPayConfig.getAppid());
        // 商户号ID
        paramsMap.put("mchid", wxPayConfig.getMchid());
        // 订单描述信息
        paramsMap.put("description", orderInfo.getTitle());
        //商户系统内部订单号
        paramsMap.put("out_trade_no", orderInfo.getOrderNo());
        //支付接口通知接口
        String notifyUrl = wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType());
        paramsMap.put("notify_url", notifyUrl);
        Map amountMap = new HashMap(2);
        //金额 单位：分
        amountMap.put("total", orderInfo.getTotalFee());
        //币种
        amountMap.put("currency", "CNY");
        //金额
        paramsMap.put("amount", amountMap);

        //将参数转换成json字符串
        String body = gson.toJson(paramsMap);
        log.info(">>>>>>>>>>> {} >> body: {}", "请求Body生成完成", body);
        return body;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOrder(HashMap<String, Object> bodyMap) {
        log.info("{} ", "处理订单信息");
        Gson gson = new Gson();
        //解密信息
        String orderDetails = decryptFromResource(bodyMap);
        HashMap<String, Object> orderMap = gson.fromJson(orderDetails, HashMap.class);
        //获取订单号
        String outTradeNo = (String) orderMap.get("out_trade_no");
        log.info("系统订单号 >>> {} ", outTradeNo);
        //处理重复通知
        String state = orderInfoService.getStateByOrderNo(outTradeNo);
        if (state.equals(OrderStatus.NOTPAY.getType())) {
            //更新订单状态
            orderInfoService.updateStatusByOrderNo(outTradeNo, OrderStatus.SUCCESS);
            //记录支付日志
            paymentInfoService.createPaymentInfo(orderDetails);
            return;
        }
        log.info(">>> {} ", "订单处理重复提交");

    }

    private String decryptFromResource(HashMap<String, Object> bodyMap) {
        log.info(">>> {} ", "密文解密");
        Gson gson = new Gson();
        //通知数据
        Map<String, String> resource = (Map<String, String>) bodyMap.get("resource");
        //数据密文
        String ciphertext = resource.get("ciphertext");
        //随机串
        String nonce = resource.get("nonce");
        //附加数据
        String associatedData = resource.get("associated_data");
        AesUtil aesUtil = new AesUtil(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        String orderDetails;
        try {
            orderDetails = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8), ciphertext);
        } catch (GeneralSecurityException e) {
            log.error(">>> {}", "密文解析失败");
            throw new RuntimeException(e);
        }
        return orderDetails;
    }
}
