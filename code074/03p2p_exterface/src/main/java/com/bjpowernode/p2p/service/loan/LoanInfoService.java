package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {

    Double queryHistoryAvgRate();

    List<LoanInfo> queryProductListBy(Map<String, Object> map);

    PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> map);

    LoanInfo queryLoanInfoById(Integer id);
}
