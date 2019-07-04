package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.LoanInfoService;
import com.bjpowernode.p2p.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoanInfoController {
    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/index.do")
    public ModelAndView index(){
        ModelAndView mv=new ModelAndView();
        //获取平均的年利率
        Double avgRate=loanInfoService.queryHistoryAvgRate();
        mv.addObject(Constants.HISTORY_AVG_RATE, avgRate);

        //获取总人数
        Long sumUserCount=userService.querySumUserCount();
        mv.addObject(Constants.SUM_USER_COUNT, sumUserCount);
        //获取总投资数
        Double sumBidMoney=bidInfoService.querySumBidMoney();
        mv.addObject(Constants.SUM_BID_MONEY, sumBidMoney);

        //获取0新手宝
        //需要的参数  开始页码  当前条数  productType
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put(Constants.PAGE_NO,0);
        paramMap.put(Constants.PAGE_SIZE, 1);
        paramMap.put(Constants.PRODUCT_TYPE, 0);
        List<LoanInfo> xlist=loanInfoService.queryLoanListBy(paramMap);
        mv.addObject("xLoanList", xlist);
        //获取1优选产品
        paramMap.put(Constants.PAGE_SIZE, 4);
        paramMap.put(Constants.PRODUCT_TYPE, 1);
        List<LoanInfo> ylist=loanInfoService.queryLoanListBy(paramMap);
        mv.addObject("yLoanList", ylist);
        //获取2散标
        paramMap.put(Constants.PAGE_SIZE, 8);
        paramMap.put(Constants.PRODUCT_TYPE, 2);
        List<LoanInfo> slist=loanInfoService.queryLoanListBy(paramMap);
        mv.addObject("sLoanList", slist);
        mv.setViewName("index");
        return mv;
    }

    @RequestMapping(value = "/loan/loan.do")
    public String loan(Model model) {


        return "loanInfo";
    }

}
