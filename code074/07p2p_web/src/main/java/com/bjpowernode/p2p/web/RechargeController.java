package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpowernode.p2p.config.AlipayConfig;
import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultVO;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.bjpowernode.p2p.utils.DateUtils;
import com.bjpowernode.p2p.utils.HttpClientUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class RechargeController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private RechargeRecordService rechargeRecordService;

    @RequestMapping(value = "/loan/toalipayRecharge")
    public String alipayRecharge(HttpServletRequest request, Model model,
                               @RequestParam(value = "rechargeMoney")Double money) {
       //System.out.println("阿里支付"+money);
        //生成一个未支付订单
        //取出用户的信息
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        //生成一个商户的订单号
        String rechargeNo = DateUtils.getDateTemp()+redisService.getOnlyNum();
        System.out.println(rechargeNo);
        //组合参数
        RechargeRecord rechargeRecord=new RechargeRecord();
        rechargeRecord.setUid(user.getId());
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeMoney(money);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("支付宝支付");

        //添加一个记录
        Integer count =rechargeRecordService.addRechargeRecord(rechargeRecord);
        if (count != 1) {
            //生成失败
            model.addAttribute("trade_msg", "添加记录失败");
            return "toRechargeBack";
        } else {
            //
            //重定向到支付宝的接口
            //System.out.println(count);
            //return "redirect:http://localhost:8083/p2p_pay/api/alipay?out_trade_no="+rechargeNo+"&total_amount="+money+"&subject=支付宝充值";
            //跳到一个页面,在该页面进行表单的自动提交,将参数使用表单进行传递
            model.addAttribute("out_trade_no", rechargeNo);
            model.addAttribute("total_amount", money);
            model.addAttribute("subject", "支付宝充值");
            return "p2pAlipay";

        }


       /*//生成一个未支付的订单
        //获取用户
        User user= (User) request.getSession().getAttribute(Constants.USER_SESSION);
        //获取商户订单
        String rechargeNo = DateUtils.getDateTemp() + redisService.getOnlyNum();
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(user.getId());
        rechargeRecord.setRechargeMoney(money);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("支付宝支付");
        rechargeRecord.setRechargeNo(rechargeNo);
        //0未支付 1支付成功 2支付失败
        rechargeRecord.setRechargeStatus("0");

        int rechargCount = rechargeRecordService.addRechargeRecord(rechargeRecord);
        if (1!=rechargCount) {
            model.addAttribute("trade_msg", "添加记录失败");
            return "toRechargeBack";
        }
        //由于需要页面进行跳转所以使用的是重定向
        //
        model.addAttribute("out_trade_no", rechargeNo);
        model.addAttribute("total_amount", money);
        model.addAttribute("subject", "支付宝充值");
        return "p2pAlipay";
*/
    }


    @RequestMapping(value = "/loan/alipayreturnBack")
    public String alipayBack(HttpServletRequest request,Model model,
                             @RequestParam(value = "trade_no")String trade_no,
                             @RequestParam(value = "out_trade_no")String out_trade_no,
                             @RequestParam(value = "total_amount")Double total_amount) throws Exception {

        //验证签名
        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        //获取验证的结果
        if (!signVerified) {
            //验签失败,充值失败
            model.addAttribute("trade_msg", "验证签名失败");
            return "toRechargeBack";
        }
        //验签成功
        //调用pay查询支付结果
        Map<String,String> parMap=new HashMap<>();
        parMap.put("trade_no", trade_no);
        String jsonStr = HttpClientUtils.doGet("http://localhost:8083/p2p_pay/api/queryalipayResult", parMap);
        System.out.println(jsonStr);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        System.out.println(jsonObject.toString());
        //获取状态码
        JSONObject refundResponse = jsonObject.getJSONObject("alipay_trade_query_response");
        String code = refundResponse.getString("code");
        if (!StringUtils.equals("10000", code)) {
            //请求失败
            model.addAttribute("trade_msg", "通信异常,请求失败");
            return "toRechargeBack";
        }
        //查询订单状态
        String tradeStatus = refundResponse.getString("trade_status");
              /*WAIT_BUYER_PAY	交易创建，等待买家付款
                TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
                TRADE_SUCCESS	交易支付成功
                TRADE_FINISHED	交易结束，不可退款*/

        if (StringUtils.equalsIgnoreCase("TRADE_CLOSED", tradeStatus)) {
            //订单充值失败,更改状态为2
            RechargeRecord parRecharge=new RechargeRecord();
            parRecharge.setRechargeNo(out_trade_no);
            parRecharge.setRechargeStatus("2");
            int count = rechargeRecordService.modifyRechargeRecordByRechargeNo(parRecharge);
            model.addAttribute("trade_msg", "充值失败,订单状态异常");
            return "toRechargeBack";
        } else if (StringUtils.equalsIgnoreCase("TRADE_SUCCESS", tradeStatus)) {
            //充值成功
            //更改订单状态   更新用户的账户余额 参数  uid  /  money  /   商家订单  有关联需要事务
            User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("uid", user.getId());
            paramMap.put("rechargeMoney", total_amount);
            paramMap.put("tradeNo", out_trade_no);

            ResultVO vo = rechargeRecordService.recharge(paramMap);
            if (!StringUtils.equalsIgnoreCase(vo.getShowCode(), Constants.SUCCESS)) {
                //数据更改失败
                model.addAttribute("trade_msg", "数据库操作失败");
                return "toRechargeBack";
            }

            //成功了
            return "redirect:/loan/myCenter";
        } else {
            model.addAttribute("trade_msg", "充值失败,订单状态异常22222");
            return "toRechargeBack";
        }


    }

    @RequestMapping(value = "/loan/towxpayRecharge")
    public String wxpayRecharge(HttpServletRequest request,Model model,
                                @RequestParam(value = "rechargeMoney")Double money) {
        System.out.println("微信支付"+money);
        //生成一个订单
        //获取用户的信息
        User user= (User) request.getSession().getAttribute(Constants.USER_SESSION);
        String rechargeNo = DateUtils.getDateTemp() + redisService.getOnlyNum();

        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(user.getId());
        rechargeRecord.setRechargeStatus("0");
        rechargeRecord.setRechargeNo(rechargeNo);
        rechargeRecord.setRechargeDesc("微信支付");
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeMoney(money);
        int count = rechargeRecordService.addRechargeRecord(rechargeRecord);
        System.out.println(count);
        if (count!=1) {
            model.addAttribute("trade_msg", "wx记录添加失败");
            return "toRechargeBack";
        }
        //将信息显示在页面
        model.addAttribute("rechargeNo",rechargeNo);
        model.addAttribute("rechargeMoney", money);
        model.addAttribute("rechargeTime", new Date());
        //跳转在支付二维码的页面
        return "showQRcode";
    }

    @RequestMapping(value = "/loan/generateQRCode")
    public void getQRcode(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "rechargeNo")String rechargeNo,
                          @RequestParam(value = "rechargeMoney")String rechargeMoney) throws Exception {
        //在这里调用pay的接口
        //System.out.println(rechargeNo);
        //System.out.println(rechargeMoney);
        Map<String,String> parMap = new HashMap<>();
        parMap.put("body", "购买金融理财");
        parMap.put("out_trade_no", rechargeNo);
        parMap.put("total_fee", rechargeMoney);
        String jsonStr = HttpClientUtils.doGet("http://localhost:8083/p2p_pay/api/wxpay", parMap);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        //System.out.println(jsonStr);
        //判断通信结果
        String return_code = jsonObject.getString("return_code");
        if (!StringUtils.equalsIgnoreCase(return_code, Constants.SUCCESS)) {
            response.sendRedirect(request.getContextPath()+"/toRechargeBack.jsp");
            return;
        }

        //判断结果是否成功
        String result_code = jsonObject.getString("result_code");
        if (!StringUtils.equalsIgnoreCase(result_code, Constants.SUCCESS)) {
            response.sendRedirect(request.getContextPath()+"/toRechargeBack.jsp");
            return;
        }
        //获取结果
        String code_url = jsonObject.getString("code_url");

        //创建二维码给前端
        Map<EncodeHintType,String> encodeHintTypeStringMap = new HashMap<>();
        encodeHintTypeStringMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE,200,200,encodeHintTypeStringMap);

        //创建一个相应流
        OutputStream outputStream = response.getOutputStream();

        //将二维码相应
        MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
        outputStream.close();
        outputStream.flush();
    }

}
