package com.bjpowernode.p2p.web;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidMoneyTopVO;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.FinanceAccountService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoanInfoController  {
    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private FinanceAccountService financeAccountService;


    @RequestMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request, Model model,
                        @RequestParam(value = "ptype",required = false) Integer ptype,
                        @RequestParam(value = "pageNo",required = false) Integer pageNo){
        //需要完成一个产品的分页展示
        /*
        * 说明:前端需要进行分页,后端需要提供的数据有
        *    每页的数据(List)/总条数(int )/计算总页数( int/每页个数 ) / 当前页数
        *    前端需要提供参数:   产品的类型 / 当前的页码  / 每页显示的个数
        *
        * */
        //使用一个map传递参数
        Map<String,Object> map=new HashMap<>();
        map.put("productType", ptype);
        //因为两个参数都不一定有值 ,有就使用 , 没有就说明是第一页
        if (null==pageNo) {
            pageNo=1;
        }
        int pageSize = 9;
        map.put("pageNo", (pageNo-1)*pageSize);

        map.put("pageSize", pageSize);

        //使用service进行值的查询
        PaginationVO<LoanInfo> paginationVO=loanInfoService.queryLoanInfoByPage(map);

        //计算总页数
        int pageList=paginationVO.getTotal().intValue() % pageSize == 0 ?
                paginationVO.getTotal().intValue() / pageSize :
                (paginationVO.getTotal().intValue() + 1) / pageSize ;

        //当前页数就是原来传过来的
        model.addAttribute("dataList", paginationVO.getDataList());
        model.addAttribute("total", paginationVO.getTotal());
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pageList", pageList);

        //当当前的位置已经有了类型时,
        if (null!=ptype) {
            model.addAttribute("productType", ptype);
        }


        //需要完成一个产品的排行榜
        //此时投资的信息都放在redis中直接取出就可以
        //由于返回的结果来自多个表有3种方式进行数据的承接   1.ListMap  2.VO  3.ObjectMap
        //先从redis中按照顺序取出数据
        //需要的参数 不用参数  在redis中的phone已经做出了累加
        List<BidMoneyTopVO> bidMoneyTopVOList = bidInfoService.queryBidMoneyTop();
        model.addAttribute("bidMoneyTop", bidMoneyTopVOList);
        return "loan";
    }

    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                            @RequestParam(value = "id",required = true) Integer id){
        //显示产品的详情
        LoanInfo loanInfo=loanInfoService.queryLoanInfoById(id);
        model.addAttribute("loanInfo", loanInfo);

        //显示投资记录(带有用户的) 需要显示前10条
        Map<String,Object> bidMap=new HashMap<>();
        bidMap.put("pageNo", 0);
        bidMap.put("pageSize", 10);
        bidMap.put("loanId", id);
        List<BidInfo> bidInfoList=bidInfoService.queryBidInfoListByLoanId(bidMap);
        model.addAttribute("bidInfoList", bidInfoList);

        //显示账户的可用余额
        //取出user
        User user= (User) request.getSession().getAttribute(Constants.USER_SESSION);
        if (null!=user) {
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());
            model.addAttribute("financeAccount", financeAccount);
        }
        return "loanInfo";
    }


}
