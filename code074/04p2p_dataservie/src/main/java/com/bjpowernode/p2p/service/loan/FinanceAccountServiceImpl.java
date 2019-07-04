package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinanceAccountServiceImpl implements FinanceAccountService{
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public FinanceAccount queryAvailableMoneyByUid(Integer uid) {

        return financeAccountMapper.selectFinanceAccountByUid(uid);
    }

    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer userId) {
        return financeAccountMapper.selectFinanceAccountByUid(userId);
    }
}
