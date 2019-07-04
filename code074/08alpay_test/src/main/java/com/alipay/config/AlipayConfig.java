package com.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016093000630172";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCJHQu/QTM1t6AHjQSB8z+ZpvkwuqgsFB4DaqBnl/S8MJVxxYHxA1oGWY1HaF2nGqAPVIaGw4nnEzY1OPwAkgmU4V86u9jejdJ+Apm7RpNcGmJHdNqEIZIhN+ybgaz3JVlw/CY8Z2JXXTKm6cJNWwY8+fPboEZ9hIgVjilSCqyknmxjRhMT0mOf7hofc1nVUMUrwOGWSnoVjfMjfxoswtct3DIjLnaw16bawSOzZz4AUGSHP0wJF+vWP+R9FbY4somNTDCpsRp1b+fOLgOFHJ93FBnaSX8jWeVQqBxdQRGswEmuykrthmEQlc0IVUqaHJLPImZq+DUt/1FCp9/aP0q5AgMBAAECggEAcEcKsYiQiLJfJw/A2zD3qbjjYZXX1JB8q0uLIol6bQhTuCCF8XhPJdf7Krg5yHazDxqmNoC00jYXM6oAIe90TSfoNU5Rnpz/+xPiE0sNrGQmimET9xMOeIk1BXpAyvkYrr6sCNaX3vaAXdIZvAKHrzL3D6fFUU9PqGcLSxVWnk2sn8XyLKuAehED1O6TcUZWWz3arg77JGMjPLMGYqB+d2couf+WgEolkJCo8mkN6BKxl4IPURwrN17iQiL8Fn7upylikTm0Cr1QyeXx8Mzl67gOjQs3neLvZAMBatSi4y5kQPQzjON1RObP9z3CRVODomuRO6uKKFy7qC/Mk8qtIQKBgQDgQ+KjUv+dGjt1LthgZYhybdQhAiZCitvrfkWHq1x+m0S5OlXP85p3+WqsS4/MUe3QUjAPcwl/gXV5tjyZ13DG78E3IWTQ4zPQOulNWZaULM5RcBYRlVdBvc32+mHxXrz9QyXBul45EHyP54ArdLOyaOcxla0a7aKdDQhca6/yNQKBgQCchAwkK6ZXBkvpq3kYI39vhJ00OpsHTGxd8xCDrmN4Fdn/XiK2Nufh3bE1V5gX/FP5WsZVdlFM6qSImG/tBfzYATZSwI6WdylxU5AjtdtSt4Ll4VMW9cBACH55ZBogPSOiSbvP9NpGxrsxq3xb+JvKIdRrNURbtY2jFHQSaFpG9QKBgCQCYnKtEgQMwT1In0+jHeyZu/Ts3m3S1AsVBF8LYdemYLuJJVw1ljjrYHh3zYgWI6ROfg8M/pptlApN+58MG7ylpJWQlBqCUMB/pRRZbTcvlpU29SQvoYoZyud8Ppu+BYR48qgPn69z+Tw2BLt1iqD7/RtWGCx5uaGUBSKp2999AoGANfMWNEmPdqPG+u6OORHTVV9CyMQCuAtRtziXUqyDwNrXX7zU/jYCy8oc0SQ2Wt4xG9x8TO0RrQ+cUzEoM21C8DnKvc991sOQV23UW1rOFL8IsbBvcZSYBfpnjfRBRSeN5ty/jjj6HDdYQxC3NwSEvrHWhHXB8FKuVa4xLD8oDxkCgYEApDLAWMXY+Bf0HJZA3dzixDMgRYBlDWOgjayZO26iPu/nx3XOm3vgR6yheS8ISp17f9V8lhLkP59BHRLFxYv14vPkRBliR+sD3dsKCXnTNmsMLZ6Ms3seWdXmmLhFvI52MwdWanxvXnTM2PFZt0OyuumRjJt29VVXpBXMtRiCYvM=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt9MqLIb7jbWu13xMSnoWSae99BxnO966iM2Mk/Y42xy4OfWBERN8MEKXLf2wkHiUDcQ6ai1uXWRf7nuZ8rYtDNc8UVRtNhlSuCJ+9F+0XzTvT7/qkrETedms2q9G8XqkmdeuAPzHCWQ0eJ6LqoSfZ0crJGwHOJuzeVoiA4gtSXVXTXTygnnaM7+FIPlsrwlU+tPJiZS3ZAI27IyjEh25JnOb0Fuv/97LQqrlr8dFDNrK0PQgAEOFAXGOHB7dLrqNeRb8EeUUGEPxj2E+Effyz683id94g/O5kuLiyJoWJ9uT9I1UyiQ11AOOARNDs9S2YvRqb+I+2Nv/R4rynVgYbQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8089/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8089/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

