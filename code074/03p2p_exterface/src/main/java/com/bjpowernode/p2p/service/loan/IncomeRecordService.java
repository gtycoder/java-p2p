package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.IncomeRecord;

import java.util.List;
import java.util.Map;

public interface IncomeRecordService {
    List<IncomeRecord> queryIncomeRecordListByUid(Map<String, Object> parMap);

    void rateIncomePlan();

    void rateIncomeBack();
}
