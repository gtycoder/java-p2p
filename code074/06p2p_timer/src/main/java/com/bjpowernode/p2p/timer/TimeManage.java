package com.bjpowernode.p2p.timer;

import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class TimeManage {
    @Autowired
    private IncomeRecordService incomeRecordService;

    private Logger logger= LogManager.getLogger(TimeManage.class);

//    @Scheduled(cron = "0/10 * * * * ?")
    public void rateIncomePlan() {
        logger.info("-------------开始计算收益-----------------");
        incomeRecordService.rateIncomePlan();
        logger.info("-------------结束计算收益-----------------");

    }
    @Scheduled(cron = "0/10 * * * * ?")
    public void rateIncomeBack() {
        logger.info("-------------开始返还收益-----------------");
        incomeRecordService.rateIncomeBack();
        logger.info("-------------结束返还收益-----------------");

    }

}
