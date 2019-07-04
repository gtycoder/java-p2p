package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.loan.LoanInfo;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {

    Double queryHistoryAvgRate();

    List<LoanInfo> queryLoanListBy(Map<String, Object> paramMap);
}
