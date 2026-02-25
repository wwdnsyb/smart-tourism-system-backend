package com.example.smarttourism.controller; // ⚠️ 注意：这里换成你自己的包名！

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alipay")
@CrossOrigin // 允许 Vue 前端跨域请求
public class AliPayController {

    // 自动读取你在 yml 里配置的参数
    @Value("${alipay.appId}")
    private String appId;
    @Value("${alipay.appPrivateKey}")
    private String appPrivateKey;
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;
    @Value("${alipay.gatewayUrl}")
    private String gatewayUrl;
    @Value("${alipay.returnUrl}")
    private String returnUrl;
    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    /**
     * 前端点“立即购买”时，调用这个接口获取支付页面
     */
    @GetMapping("/pay")
    public String pay(@RequestParam String orderId,
                      @RequestParam String amount,
                      @RequestParam String subject) throws AlipayApiException {

        // 1. 初始化支付宝客户端（相当于建立和支付宝服务器的专属加密通道）
        AlipayClient alipayClient = new DefaultAlipayClient(
                gatewayUrl, appId, appPrivateKey, "json", "UTF-8", alipayPublicKey, "RSA2");

        // 2. 创建一个“电脑网站支付”的请求
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        // 3. 把订单号、金额、商品名字打包装进请求里
        request.setBizContent("{\"out_trade_no\":\"" + orderId + "\","
                + "\"total_amount\":\"" + amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        // 4. 发送请求！支付宝会返回一段自带二维码的 HTML 表单代码
        return alipayClient.pageExecute(request).getBody();
    }
}