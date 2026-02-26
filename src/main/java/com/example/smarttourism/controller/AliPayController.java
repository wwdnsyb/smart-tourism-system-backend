package com.example.smarttourism.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.alipay.api.internal.util.AlipaySignature;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
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
    /**
     * 🚀 支付宝异步回调接口：支付成功后，支付宝会自动调用这里
     */
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws Exception {
        // 1. 获取支付宝 POST 过来的所有参数，装进 Map 里
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }

        // 2. 🛡️ 核心大招：调用支付宝 SDK 验证签名（防伪造！）
        // 必须验证，防止黑客自己发个请求说“我付钱了”
        boolean signVerified = AlipaySignature.rsaCheckV1(
                params, alipayPublicKey, "UTF-8", "RSA2");

        if (signVerified) {
            // 3. 验签通过！确实是支付宝官方发来的通知
            String tradeStatus = params.get("trade_status"); // 获取交易状态

            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                String orderId = params.get("out_trade_no");  // 获取你的系统里的订单号
                String alipayTradeNo = params.get("trade_no"); // 获取支付宝官方的流水号
                String totalAmount = params.get("total_amount"); // 获取实付金额

                System.out.println("=========================================");
                System.out.println("🎉 收到支付宝成功通知！");
                System.out.println("订单号：" + orderId);
                System.out.println("支付宝流水号：" + alipayTradeNo);
                System.out.println("实付金额：" + totalAmount);
                System.out.println("=========================================");

                // 🛑 TODO: 这里是你唯一需要写业务逻辑的地方！
                // 比如：orderService.updateOrderStatusToPaid(orderId);
                // 去数据库里，把这个 orderId 对应的订单状态改成 "已支付"！
            }

            // 4. 必须给支付宝返回 "success" 这7个字母
            // 否则支付宝会觉得你没收到，在接下来的24小时内一直给你发通知（夺命连环call）
            return "success";
        } else {
            // 验签失败
            System.out.println("⚠️ 警告：收到伪造的支付宝回调请求！");
            return "fail";
        }
    }
}