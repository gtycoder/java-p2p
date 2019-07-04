package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.vo.ResultVO;

import java.util.List;
import java.util.Map;

public interface RechargeRecordService {
    List<RechargeRecord> queryRechargeRecordsByUis(Map<String, Object> parMap);

    Integer addRechargeRecord(RechargeRecord rechargeRecord);

    int modifyRechargeRecordByRechargeNo(RechargeRecord parRecharge);

    ResultVO recharge(Map<String, Object> paramMap);
}
