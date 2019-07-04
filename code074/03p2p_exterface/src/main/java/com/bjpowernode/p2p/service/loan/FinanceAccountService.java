package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.user.FinanceAccount;

public interface FinanceAccountService {

    FinanceAccount queryAvailableMoneyByUid(Integer uid);

    FinanceAccount queryFinanceAccountByUid(Integer userId);
}
