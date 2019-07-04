package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private BidInfoService bidInfoService;


    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request, Model model){
        //获取年化的历史平均利率
        Double historyAvgRate=loanInfoService.queryHistoryAvgRate();
        model.addAttribute(Constants.HISTORY_AVG_RATE, historyAvgRate);
        //获取平台的人数
        long count=userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USERS_COUNT, count);
        //获取平台的累计投资金额
        Double sumBidMoney = bidInfoService.querySumBidMoney();
        model.addAttribute(Constants.SUM_BID_MONEY, sumBidMoney);
        //获取0新手宝
        /*使用的是map方式进行传递值
         * 以分页的方式进行展示
         *
         */
        Map<String,Object> map=new HashMap<>();
        map.put("pageNo", 0);
        map.put("pageSize", 1);
        map.put("productType", Constants.PRODUCT_TYPE_X);
        List<LoanInfo> xLoanInfo=loanInfoService.queryProductListBy(map);
        model.addAttribute("xLoanInfo",xLoanInfo );

        //获取1优选产品
        map.put("pageSize", 4);
        map.put("productType", Constants.PRODUCT_TYPE_Y);
        List<LoanInfo> yLoanInfo=loanInfoService.queryProductListBy(map);
        model.addAttribute("yLoanInfo", yLoanInfo);
        //获取2散标产品
        map.put("pageSize", 8);
        map.put("productType", Constants.PRODUCT_TYPE_S);
        List<LoanInfo> sLoanInfo = loanInfoService.queryProductListBy(map);
        model.addAttribute("sLoanInfo", sLoanInfo);

        return "index";
    }



}
