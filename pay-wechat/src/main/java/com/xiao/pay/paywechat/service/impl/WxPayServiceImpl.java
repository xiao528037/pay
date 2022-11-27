package com.xiao.pay.paywechat.service.impl;

import com.google.gson.Gson;
import com.xiao.pay.paywechat.config.WxPayConfig;
import com.xiao.pay.paywechat.dao.ProductMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.enums.wxpay.WxApiType;
import com.xiao.pay.paywechat.enums.wxpay.WxNotifyType;
import com.xiao.pay.paywechat.service.OrderInfoService;
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
import org.springframework.util.Assert;

import java.io.IOException;
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


    public WxPayServiceImpl(ProductMapper productMapper, OrderInfoService orderInfoService, WxPayConfig wxPayConfig, CloseableHttpClient wxPayClient) {
        this.productMapper = productMapper;
        this.orderInfoService = orderInfoService;
        this.wxPayConfig = wxPayConfig;
        this.wxPayClient = wxPayClient;
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
        paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));
        Map amountMap = new HashMap();
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
}
