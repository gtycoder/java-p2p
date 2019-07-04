package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.BidMoneyTopVO;
import com.bjpowernode.p2p.model.vo.ResultVO;

import java.util.List;
import java.util.Map;

public interface BidInfoService {
    Double querySumBidMoney();

    List<BidInfo> queryBidInfoListByLoanId(Map<String, Object> bidMap);

    List<BidInfo> queryBidInfoListByUid(Map<String, Object> parMap);

    ResultVO invest(Map<String, Object> parMap);

    List<BidMoneyTopVO> queryBidMoneyTop();
}
