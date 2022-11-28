package com.xiao.pay.paywechat.service.impl;

import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.xiao.pay.paywechat.config.WxPayConfig;
import com.xiao.pay.paywechat.dao.ProductMapper;
import com.xiao.pay.paywechat.entity.OrderInfo;
import com.xiao.pay.paywechat.entity.RefundInfo;
import com.xiao.pay.paywechat.enums.OrderStatus;
import com.xiao.pay.paywechat.enums.wxpay.WxApiType;
import com.xiao.pay.paywechat.enums.wxpay.WxNotifyType;
import com.xiao.pay.paywechat.enums.wxpay.WxTradeState;
import com.xiao.pay.paywechat.service.OrderInfoService;
import com.xiao.pay.paywechat.service.PaymentInfoService;
import com.xiao.pay.paywechat.service.RefundsInfoService;
import com.xiao.pay.paywechat.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-27 13:57:09
 * @description
 */
@Service("wxPayService")
@Slf4j(topic = "WxPayServiceImpl")
@SuppressWarnings("all")
public class WxPayServiceImpl implements WxPayService {

    private final ProductMapper productMapper;

    private final OrderInfoService orderInfoService;

    private final WxPayConfig wxPayConfig;

    private final CloseableHttpClient wxPayClient;

    private final CloseableHttpClient wxPayDownloadClient;

    private final PaymentInfoService paymentInfoService;

    private final RefundsInfoService refundsInfoService;

    private final Verifier verifier;
    private final ReentrantLock lock = new ReentrantLock();

    public WxPayServiceImpl(ProductMapper productMapper,
                            OrderInfoService orderInfoService,
                            WxPayConfig wxPayConfig,
                            CloseableHttpClient wxPayClient,
                            CloseableHttpClient wxPayDownloadClient, PaymentInfoService paymentInfoService,
                            RefundsInfoService refundsInfoService,
                            Verifier verifier) {
        this.productMapper = productMapper;
        this.orderInfoService = orderInfoService;
        this.wxPayConfig = wxPayConfig;
        this.wxPayClient = wxPayClient;
        this.wxPayDownloadClient = wxPayDownloadClient;
        this.paymentInfoService = paymentInfoService;
        this.refundsInfoService = refundsInfoService;
        this.verifier = verifier;
    }

    @Override
    public Map nativePay(Long productId) throws IOException {
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
        } catch (IOException e) {
            log.info(">>>>>>> {} ", "下单失败");
            throw new RuntimeException(e);
        } finally {
            response.close();
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
        //处理重复通知,防止并发操作
        if (lock.tryLock()) {
            try {
                String state = orderInfoService.getStateByOrderNo(outTradeNo);
                if (state.equals(OrderStatus.NOTPAY.getType())) {
                    //更新订单状态
                    orderInfoService.updateStatusByOrderNo(outTradeNo, OrderStatus.SUCCESS);
                    //记录支付日志
                    paymentInfoService.createPaymentInfo(orderDetails);
                    return;
                }
                log.info(">>> {} ", "订单处理重复提交");
            } catch (Exception e) {
                log.error(">>> {} ", "订单处理发生异常");
                throw new RuntimeException("订单处理发生异常");
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        log.info(">>> {} >>> 订单编号 >>> {}", "关闭订单", orderNo);
        Gson gson = new Gson();
        String url = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), orderNo);
        url = wxPayConfig.getDomian().concat(url);
        HttpPost httpPost = new HttpPost(url);
        //请求体
        HashMap<String, String> paramsMap = new HashMap<>(2);
        paramsMap.put("mchid", wxPayConfig.getMchid());
        String jsonParams = gson.toJson(paramsMap);
        log.info("请求参数 >>> {} ", jsonParams);
        //生成请求参数
        StringEntity stringEntity = new StringEntity(jsonParams, "utf-8");
        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Accept", "application/json");
        CloseableHttpResponse response = null;
        try {
            response = wxPayClient.execute(httpPost);
            //响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
                log.info("取消订单成功 ");
            } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
                orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CANCEL);
                log.info("{} ", "请求成功");
            } else {
                log.info("Native 取消订单失败,状态码 >>> {} ", statusCode);
                Assert.isTrue(false, "请求微信支付取消订单失败");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    /**
     * 解密
     *
     * @param bodyMap
     * @return
     */
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

    @Override
    public String queryOrder(String orderNo) {
        log.info("查询订单接口 >>> {} ", orderNo);
        String url = String.format(WxApiType.ORDER_QUERY_BY_NO.getType(), orderNo);
        url = wxPayConfig.getDomian().concat(url).concat("?mchid=").concat(wxPayConfig.getMchid());
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        String body;
        try {
            CloseableHttpResponse response = wxPayClient.execute(httpGet);
            //获取响应体
            body = EntityUtils.toString(response.getEntity());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return body;
    }

    @Override
    public void checkOrderStatus(String orderNo) {
        log.info("根据订单号合适订单状态{} ", orderNo);
        String result = this.queryOrder(orderNo);
        Gson gson = new Gson();
        HashMap resultMap = gson.fromJson(result, HashMap.class);
        //获取微信支付段的订单状态
        String tradeState = (String) resultMap.get("trade_state");

        if (tradeState.equals(WxTradeState.SUCCESS.getType())) {
            log.warn("订单已经支付 >>> {}", orderNo);
            //修改本地订单状态
            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.SUCCESS);
            //记录日志信息
            paymentInfoService.createPaymentInfo(result);
        }
        if (tradeState.equals(WxTradeState.NOTPAY.getType())) {
            //未支付，关单，更新本地订单状态
            cancelOrder(orderNo);
            //修改本地订单状态
            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.CLOSED);
        }
    }

    @Override
    public void refund(String orderNo, String reason) throws IOException {
        log.info("{} ", "创建退款单记录");
        //根据订单编号创建退款单
        RefundInfo refundInfo = refundsInfoService.createRefundByOrderNo(orderNo, reason);
        log.info("{} ", "调用退款API");

        //调用统一下单API
        String url = wxPayConfig.getDomian().concat(WxApiType.DOMESTIC_REFUNDS.getType());
        HttpPost httpPost = new HttpPost(url);

        //请求body参数
        Gson gson = new Gson();
        HashMap paramsMap = new HashMap<>();
        //订单编号
        paramsMap.put("out_trade_no", orderNo);
        //退款单编号
        paramsMap.put("out_refund_no", refundInfo.getRefundNo());
        //退款原因
        paramsMap.put("reason", reason);
        //退款通知地址
        paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.REFUND_NOTIFY.getType()));

        HashMap amountMap = new HashMap();
        //退款金额
        amountMap.put("refund", refundInfo.getRefund());
        //金额
        amountMap.put("total", refundInfo.getTotalFee());
        //退款币种
        amountMap.put("currency", "CNY");
        paramsMap.put("amount", amountMap);

        String jsonParams = gson.toJson(paramsMap);
        log.info("请求参数 ===> {} ", jsonParams);

        StringEntity entity = new StringEntity(jsonParams, "utf-8");
        entity.setContentType("application/json");//设置请求报文格式
        httpPost.setEntity(entity);//将请求报文放入请求对象
        httpPost.setHeader("Accept", "application/json");//设置响应报文格式

        //完成签名并执行请求，并完成验签
        CloseableHttpResponse response = wxPayClient.execute(httpPost);

        try {
            //解析响应结果
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.info("成功, 退款返回结果 = " + bodyAsString);
            } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
                log.info("成功");
            } else {
                throw new RuntimeException("退款异常, 响应码 = " + statusCode + ", 退款返回结果 = " + bodyAsString);
            }
            //更新订单状态
            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_PROCESSING);
            //更新退款单
            refundsInfoService.updateRefund(bodyAsString);

        } finally {
            response.close();
        }
    }

    @Override
    public Map queryRefund(String refundNo) throws IOException {
        log.info("<<< 查询退款接口调用 >>> {} ", refundNo);
        String url = String.format(WxApiType.DOMESTIC_REFUNDS_QUERY.getType(), refundNo);
        url = wxPayConfig.getDomian().concat(url);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        CloseableHttpResponse response = null;
        String bodyAsString;
        try {
            response = wxPayClient.execute(httpGet);
            bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                log.info("{} >>> {}", "成功，查询退款结果返回", bodyAsString);
            } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
                log.info("{} ", "成功");
            } else {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            log.info("{} ", "获取订单信息失败");
            throw new RuntimeException(e);
        } finally {
            response.close();
        }
        Gson gson = new Gson();
        return gson.fromJson(bodyAsString, HashMap.class);
    }

    @Override
    public void processRefund(HashMap<String, Object> bodyMap) {
        log.info("{} ", "退款单处理");
        String plainText = decryptFromResource(bodyMap);
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String) plainTextMap.get("out_trade_no");
        if (lock.tryLock()) {
            String state = orderInfoService.getStateByOrderNo(orderNo);
            if (!OrderStatus.REFUND_PROCESSING.getType().equals(state)) {
                return;
            }
            //更新订单状态
            orderInfoService.updateStatusByOrderNo(orderNo, OrderStatus.REFUND_SUCCESS);
            //更新退款单
            refundsInfoService.updateRefund(plainText);
        }
    }

    /**
     * 申请账单
     *
     * @param billDate
     * @param type
     * @return
     * @throws Exception
     */
    @Override
    public String queryBill(String billDate, String type) throws Exception {
        log.warn("申请账单接口调用 {}", billDate);

        String url = "";
        if ("tradebill".equals(type)) {
            url = WxApiType.TRADE_BILLS.getType();
        } else if ("fundflowbill".equals(type)) {
            url = WxApiType.FUND_FLOW_BILLS.getType();
        } else {
            throw new RuntimeException("不支持的账单类型");
        }

        url = wxPayConfig.getDomian()
                .concat(url)
                .concat("?bill_date=")
                .concat(billDate)
                .concat("&bill_type=")
                .concat("ALL");

        //创建远程Get 请求对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");

        //使用wxPayClient发送请求得到响应
        CloseableHttpResponse response = wxPayClient.execute(httpGet);

        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 申请账单返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("申请账单异常, 响应码 = " + statusCode + ", 申请账单返回结果 = " + bodyAsString);
            }

            //获取账单下载地址
            Gson gson = new Gson();
            Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
            return resultMap.get("download_url");

        } finally {
            response.close();
        }
    }

    /**
     * 下载账单
     *
     * @param billDate
     * @param type
     * @return
     * @throws Exception
     */
    @Override
    public String downloadBill(String billDate, String type) throws Exception {
        log.warn("下载账单接口调用 {}, {}", billDate, type);

        //获取账单url地址
        String downloadUrl = this.queryBill(billDate, type);
        log.info("{} ", downloadUrl);
        //创建远程Get 请求对象
        HttpGet httpGet = new HttpGet(downloadUrl);
        httpGet.addHeader("Accept", "application/json");

        //使用wxPayClient发送请求得到响应
        CloseableHttpResponse response = wxPayDownloadClient.execute(httpGet);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 下载账单返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("下载账单异常, 响应码 = " + statusCode + ", 下载账单返回结果 = " + bodyAsString);
            }
            return bodyAsString;
        } finally {
            response.close();
        }
    }
}
