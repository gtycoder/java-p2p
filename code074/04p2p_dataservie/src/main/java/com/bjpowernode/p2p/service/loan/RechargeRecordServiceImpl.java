package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RechargeRecordServiceImpl implements RechargeRecordService{
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public List<RechargeRecord> queryRechargeRecordsByUis(Map<String, Object> parMap) {
        return rechargeRecordMapper.selectRechargeRecordListByUid(parMap);
    }

    @Override
    public Integer addRechargeRecord(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public int modifyRechargeRecordByRechargeNo(RechargeRecord parRecharge) {
        return rechargeRecordMapper.updateByRechargeNo(parRecharge);
    }

    @Override
    public ResultVO recharge(Map<String, Object> paramMap) {
        ResultVO vo = new ResultVO();
        //更改订单的状态
        RechargeRecord par = new RechargeRecord();
        par.setRechargeNo((String) paramMap.get("tradeNo"));
        par.setRechargeStatus("1");
        int count = rechargeRecordMapper.updateByRechargeNo(par);
        if (count!=1) {
            vo.setShowCode(Constants.FAIL);
            return vo;
        }
        //更新账户可用余额
        int count2 = financeAccountMapper.updateAvailableMoneyByRecharge(paramMap);
        if (count2!=1) {
            vo.setShowCode(Constants.FAIL);
            return vo;
        }
        vo.setShowCode(Constants.SUCCESS);

        return vo;
    }
}
