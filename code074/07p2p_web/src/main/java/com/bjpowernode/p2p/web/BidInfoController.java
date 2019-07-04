package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BidInfoController {
    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/loan/invest")
    @ResponseBody
    public Object invest(HttpServletRequest request,
                         @RequestParam(value = "loanId",required = true)Integer loanId,
                         @RequestParam(value = "bidMoney",required = true)Double bidMoney) {
        Map<String,Object> retMap=new HashMap<>();
        //进行投资   业务具有关联性,全部在service完成,并使用事务
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);

        //准备参数使用map
        Map<String,Object> parMap=new HashMap<>();
        parMap.put("uid", user.getId());
        parMap.put("loanId", loanId);
        parMap.put("bidMoney", bidMoney);

        //额外传递一个phone参数.用来在redis中存储
        parMap.put("phone", user.getPhone());

        //调用service方法,返回一个是否成功的标志 使用result
        ResultVO result=bidInfoService.invest(parMap);

        //进行判断
        if (!StringUtils.equalsIgnoreCase(result.getShowCode(), Constants.SUCCESS)) {
            //不成功
            retMap.put(Constants.SHOW_MESSAGE, "投资失败");
            return retMap;
        } else {
            //成功了
            retMap.put(Constants.SHOW_MESSAGE, Constants.OK);
            return retMap;
        }

    }
}
