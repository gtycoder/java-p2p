package com.bjpowernode.pay.web;

import com.bjpowernode.p2p.utils.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WxpayController {

    @RequestMapping(value = "/api/wxpay")
    @ResponseBody
    public Object wxpay(HttpServletRequest request,
                        @RequestParam(value = "body")String body,
                        @RequestParam(value = "out_trade_no")String out_trade_no,
                        @RequestParam(value = "total_fee")String total_fee) throws Exception {
        //System.out.println(total_fee);
        //http://localhost:8083/p2p_pay/api/wxpay
        //先使用map保存数据,然后使用工具包将map转为xml
        Map<String,String> parMap = new HashMap<>();
        parMap.put("appid", "wx8a3fcf509313fd74");
        parMap.put("mch_id", "1361137902");
        parMap.put("nonce_str", WXPayUtil.generateNonceStr());
        parMap.put("body", body);
        parMap.put("out_trade_no", out_trade_no);
        //当出现使用分作为单位进行传递金额的时候,一定要使用bigdic进行数据的传递,否则容易失准
        BigDecimal bigDecimal = new BigDecimal(total_fee);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        int i = multiply.intValue();
        //System.out.println(i);
        parMap.put("total_fee",String.valueOf(i));
        parMap.put("spbill_create_ip", "127.0.0.1");
        parMap.put("notify_url", "http://localhost:8080/myWeb/toRechargeResult");
        parMap.put("trade_type", "NATIVE");
        //使用现有的参数生成一个签名
        String signature = WXPayUtil.generateSignature(parMap, "367151c5fd0d50f1e34a68a802d6bbca");
        parMap.put("sign", signature);
        //将map转换为xml
        String xmlData = WXPayUtil.mapToXml(parMap);
        //发送请求
        String xmlResult = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", xmlData);

        //将结果转换成map
        Map<String, String> result = WXPayUtil.xmlToMap(xmlResult);
        return result;
    }
}
