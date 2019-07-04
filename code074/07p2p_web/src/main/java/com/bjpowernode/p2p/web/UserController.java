package com.bjpowernode.p2p.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultVO;
import com.bjpowernode.p2p.service.loan.*;
import com.bjpowernode.p2p.service.user.UserService;
import com.bjpowernode.p2p.utils.HttpClientUtils;
import com.bjpowernode.p2p.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private BidInfoService bidInfoService;
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private IncomeRecordService incomeRecordService;


    @RequestMapping(value = "/loan/checkPhone")
    public @ResponseBody Object checkPhone(HttpServletRequest request, Model model,
                      @RequestParam(value = "phone", required = true) String phone) {
        Map<String, Object> retMap = new HashMap<>();

        //根据手机号码查询用户是否存在(手机号码) -> 返回int|boolean
        //根据手机号码查询用户信息(手机号码) -> 返回User
        User user = userService.queryUserByPhone(phone);

        //判断用户是否存在
        if (null != user) {

            retMap.put(Constants.SHOW_MESSAGE, "该手机号码已被注册，请更新手机号码");
            //model.addAttribute(Constants.ERROR_MESSAGE, "该手机号码已被注册，请更新手机号码");
            return retMap;
        }

        retMap.put(Constants.SHOW_MESSAGE, Constants.OK);
        //model.addAttribute(Constants.ERROR_MESSAGE, Constants.OK);
        //retMap.put(Constants.SHOW_MESSAGE, Constants.YES);


        return retMap;
    }

    @RequestMapping(value = "/loan/checkCaptcha")
    @ResponseBody
    public Map<String, Object> checkCaptcha(HttpServletRequest request,
                                            @RequestParam(value = "captcha", required = true) String captcha) {
        Map<String, Object> retMap = new HashMap<>();
        //判断验证码是不是与session中存放的是否一致
        HttpSession session = request.getSession();
        String cap = (String) session.getAttribute(Constants.CAPTCHA);
        if (StringUtils.equalsIgnoreCase(captcha, cap)) {
            retMap.put(Constants.SHOW_MESSAGE, Constants.OK);
        } else {
            retMap.put(Constants.SHOW_MESSAGE, "请输入正确的验证码");
        }
        return retMap;
    }

    @RequestMapping(value = "/loan/register")
    @ResponseBody
    public Map<String, Object> register(HttpServletRequest request,
                                        @RequestParam(value = "phone") String phone,
                                        @RequestParam(value = "password") String password) {
        Map<String, Object> retMap = new HashMap<>();
        //将数据添加到数据库中(创建一个用户,并且开立一个账户)
        //这两的业务需要使用事务控制,
        ResultVO result = userService.Register(phone, password);

        if (StringUtils.equalsIgnoreCase(result.getShowCode(), Constants.SUCCESS)) {
            retMap.put(Constants.SHOW_MESSAGE, Constants.OK);
            //将对象放入session中进行登录
            HttpSession session = request.getSession();
            User user = userService.queryUserByPhone(phone);
            session.setAttribute(Constants.USER_SESSION, user);
        } else {
            retMap.put(Constants.SHOW_MESSAGE, "注册失败,稍后重试");
            return retMap;
        }
        return retMap;
    }

    @RequestMapping(value = "/loan/myFinanceAccount")
    @ResponseBody
    public Object myFinance(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        FinanceAccount financeAccount = financeAccountService.queryAvailableMoneyByUid(user.getId());
        return financeAccount;
    }

    @RequestMapping(value = "/loan/verifyRealName")
    @ResponseBody
    public Object verifyRealName(HttpServletRequest request,
                               @RequestParam(value = "realName",required = true)String realName ,
                               @RequestParam(value = "idCard",required = true) String idCard  ) throws Exception {
        Map<String,Object> retMap=new Hashtable<>();

        //System.out.println(idCard);
        //准备请求的参数
        Map<String,String> paramMap=new HashMap<>();
        paramMap.put("appkey", "948ee96557c4a240b9a2c7b1cbae4b41");
        paramMap.put("cardNo", idCard);
        paramMap.put("realName", realName);

        //调用远程的实名认证的接口
        //参数按照要求的发送
        String jsonString = HttpClientUtils.doGet("https://way.jd.com/VerifyIdcard/J65", paramMap);
       /*String jsonString="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"Success\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"张三\",\n" +
                "            \"idcard\": \"330329199001020022\",\n" +
                "            \"isok\": false\n" +
                "        }\n" +
                "    }\n" +
                "}";*/
        //将返回值使用fastjson进行解析
        //System.out.println(jsonString);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String code = jsonObject.getString("code");
        //查看是否成功
        if (StringUtils.equals("10000", code)) {
            //成功了进行值得取出
            Boolean isOk = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");
            if (isOk) {
                //验证成功更新数据
                HttpSession session = request.getSession();
                //取出session中的user
                User user = (User) session.getAttribute(Constants.USER_SESSION);
                //创建一个参数的user
                User paramUser=new User();
                paramUser.setId(user.getId());
                paramUser.setName(realName);
                paramUser.setIdCard(idCard);
                int i=userService.modifyUserById(paramUser);
                if (i != 1) {
                    retMap.put(Constants.SHOW_MESSAGE, "通信失败11");
                    return retMap;
                } else {
                    retMap.put(Constants.SHOW_MESSAGE, Constants.OK);
                    //更新session中值
                    user.setName(realName);
                    user.setIdCard(idCard);
                    session.setAttribute(Constants.USER_SESSION, user);

                    return retMap;
                }

            } else {
                //不成功进行错误回执
                retMap.put(Constants.SHOW_MESSAGE, "信息不一致");
                return retMap;

            }

        } else {
            //返回失败
            retMap.put(Constants.SHOW_MESSAGE, "通信失败22");
            return  retMap;
        }

    }

    @RequestMapping(value = "/loan/logout")
    public String logout(HttpServletRequest request,Model model) {
        //清除掉session中的值
        request.getSession().removeAttribute(Constants.USER_SESSION);
        //或是使session中的值失效
        //request.getSession().invalidate();
        return "redirect:/index";

    }
    @RequestMapping(value = "/loan/stage")
    @ResponseBody
    public Object stage(HttpServletRequest request) {
        Map<String,Object> retMap=new Hashtable<>();

        //获取年化平均利率
        Double avgRate = loanInfoService.queryHistoryAvgRate();

        //获取平台的人数
        long userCount = userService.queryAllUserCount();
        //获取总投资数
        Double sumBidMoney = bidInfoService.querySumBidMoney();


        retMap.put(Constants.HISTORY_AVG_RATE, avgRate);
        retMap.put(Constants.ALL_USERS_COUNT, userCount);
        retMap.put(Constants.SUM_BID_MONEY, sumBidMoney);
        return retMap;
    }

    @RequestMapping(value = "/loan/login")
    @ResponseBody
    public Object login(HttpServletRequest request,
                        @RequestParam(value = "phone")String phone,
                        @RequestParam(value = "password")String password) {
        Map<String,Object> retMap=new Hashtable<>();

        //查询user是否存在
        //存在更新最后登录时间
        User paramUser=new User();
        paramUser.setPhone(phone);
        paramUser.setLoginPassword(password);

        //进行查询
        User user=userService.login(paramUser);

        if (null == user) {
            //用户不存在
            retMap.put(Constants.SHOW_MESSAGE, "没有查到");
            return retMap;
        } else {
            //将用户放到session中进行登录
            request.getSession().setAttribute(Constants.USER_SESSION, user);
            retMap.put(Constants.SHOW_MESSAGE, Constants.OK);
            return retMap;
        }
    }

    @RequestMapping(value = "/loan/messageCode")
    @ResponseBody
    public Object messageCode(HttpServletRequest request,
                              @RequestParam(value = "phone")String phone) throws Exception {
        Map<String,Object> retMap=new Hashtable<>();

        //进行短信的验证
        Map<String,String> paramMap=new HashMap<>();
        //组合一个appkey
        paramMap.put("appkey", "948ee96557c4a240b9a2c7b1cbae4b41");
        //组合一个mobile
        paramMap.put("mobile", phone);
        //组合一个content

        String messageCode= MessageUtils.getMessage(4);
        String content="【凯信通】您的验证码是："+messageCode;

        paramMap.put("content", content);
        //String jsonString = HttpClientUtils.doGet("https://way.jd.com/kaixintong/kaixintong", paramMap);
        String jsonString="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 0,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-797342</remainpoint>\\n <taskID>89618709</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                "}";

        //解析返回的值
        //System.out.println(jsonString);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String code = jsonObject.getString("code");
        if (!StringUtils.equalsIgnoreCase("10000", code)) {
            //没有通信成功
            retMap.put(Constants.SHOW_MESSAGE, "通讯失败code");
            return retMap;
        } else {
            //成功了,查看短信是否发送成功
            String result = jsonObject.getString("result");
            //result是一个xml格式.使用dom4j和xpath进行解析  导入jar依赖 dom4j和jaxen
            //首先格式化result
            Document document = DocumentHelper.parseText(result);
            //对格式化的进行路径设置   /returnsms/returnstatus
            Node node = document.selectSingleNode("/returnsms/returnstatus");
            String text = node.getText();
            if (!StringUtils.equalsIgnoreCase(Constants.SUCCESS, text)) {
                //短信没有发送成功
                retMap.put(Constants.SHOW_MESSAGE, "发送短信失败");
                return retMap;
            } else {
                //方便测试将值传给前端
                retMap.put("messageCode", messageCode);
                //将验证码放入redis中进行存储
                redisService.set(phone,messageCode,60);
                //发送成功,告诉前端开始计时
                retMap.put(Constants.SHOW_MESSAGE, Constants.OK);
                return retMap;
            }

        }

    }

    @RequestMapping(value = "/loan/checkMessage")
    @ResponseBody
    public Object checkMessage(HttpServletRequest request,
                               @RequestParam(value = "messageCode",required = true)String messageCode,
                               @RequestParam(value = "phone",required = true)String phone) {
        Map<String,Object> retMap=new HashMap<>();
        //从redis中取出值进行验证是否可以
        //System.out.println(phone);
        String message =  redisService.get(phone);
        //System.out.println(message);
        //System.out.println(messageCode);

        if (!StringUtils.equals(message, messageCode)) {
            //不相等
            retMap.put(Constants.SHOW_MESSAGE, "验证码有误");
            return retMap;
        } else {
            //相等
            retMap.put(Constants.SHOW_MESSAGE, Constants.OK);
            return retMap;
        }

    }

    @RequestMapping(value = "/loan/myCenter")
    public ModelAndView myCenter(HttpServletRequest request) {
        ModelAndView mv=new ModelAndView();
        //获取用户的信息
        User user  = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        //根据用户信息获取资金账户
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());
        mv.addObject(Constants.FINANCE_ACCOUNT, financeAccount);
        //获取最近投资信息
        //可以看做一个分页进行操作  当前的页码  页面条数  useid
        Map<String,Object> parMap=new HashMap<>();
        parMap.put("uid", user.getId());
        parMap.put("pageNo", 0);
        parMap.put("pageSize", 5);
        List<BidInfo> bidInfoList =  bidInfoService.queryBidInfoListByUid(parMap);
        //bidInfoList.forEach(b-> System.out.println(b.getLoanInfo().getProductName()));
        mv.addObject(Constants.BID_INFO_LIST, bidInfoList);
        //获取最近充值记录
        List<RechargeRecord> rechargeRecords=rechargeRecordService.queryRechargeRecordsByUis(parMap);
        mv.addObject(Constants.RECHARGE_RECORD, rechargeRecords);

        //获取收益记录
        List<IncomeRecord> incomeRecordList=incomeRecordService.queryIncomeRecordListByUid(parMap);
        mv.addObject(Constants.INCOME_RECORD, incomeRecordList);

        mv.setViewName("myCenter");
        return mv;
    }
}
