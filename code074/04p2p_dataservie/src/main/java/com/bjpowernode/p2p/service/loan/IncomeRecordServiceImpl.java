package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IncomeRecordServiceImpl implements IncomeRecordService{
    @Autowired
    private IncomeRecordMapper incomeRecordMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public List<IncomeRecord> queryIncomeRecordListByUid(Map<String, Object> parMap) {
        return incomeRecordMapper.selectIncomeRecordListByUid(parMap);
    }

    @Override
    public void rateIncomePlan() {
        //生成收益
        //找到所有的已经满标的产品在loaninfo中  状态是1的
        List<LoanInfo> loanInfoList=loanInfoMapper.selectLoanInfoByProductStatus(1);
        //对bidinfo中的所有的该产品的投资
        for (LoanInfo loanInfo : loanInfoList) {
            //获取每一个产品记录对应的投资记录
            List<BidInfo> bidInfoList=bidInfoMapper.selectBidInfoListByLoanId(loanInfo.getId());

            for (BidInfo bidInfo : bidInfoList) {
                //对每一条记录生成一条收益记录
                IncomeRecord incomeRecord=new IncomeRecord();
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setUid(bidInfo.getUid());
                //收益状态 0未返回  1已经返还
                incomeRecord.setIncomeStatus(0);
                incomeRecord.setLoanId(bidInfo.getLoanId());
                //计算收益时间 = 满标的时间加上 +  投资的周期
                Date incomeDate = null;

                //计算收益  =  本金 * 天利率 * 天数
                Double incomeMoney = null;
                /*两个都要区分新手宝还是其他的产品*/
                if (loanInfo.getProductType() == Constants.PRODUCT_TYPE_X) {
                    //这是新手宝单位是天
                    incomeDate = DateUtils.getDateByAddDays(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney()*(loanInfo.getRate()/100/365)*loanInfo.getCycle();
                } else {
                    //这是其他的,单位是月
                    incomeDate = DateUtils.getDateByAddMonths(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney()*(loanInfo.getRate()/100/365)*loanInfo.getCycle()*30;
                }
                incomeMoney = Math.round(incomeMoney*Math.pow(10, 2))/Math.pow(10, 2);

                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                //进行插入
                incomeRecordMapper.insertSelective(incomeRecord);

            }
            //更改已经满标的产品状态  更新成2
            LoanInfo loanInfo1=new LoanInfo();
            loanInfo1.setId(loanInfo.getId());
            loanInfo1.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(loanInfo1);

        }

    }

    @Override
    public void rateIncomeBack() {
        //收益返还
        //条件 当收益状态是0  并且收益时间与当前的时间一致
        List<IncomeRecord> records=incomeRecordMapper.selectIncomeRecordByStatus(0);
           //将收益和本金返回对应的账户
        for (IncomeRecord record : records) {
            //参数需要uid  /  本金  / 收益
            Map<String,Object> parMap=new HashMap<>();
            parMap.put("uid", record.getUid());
            parMap.put("bidMoney", record.getBidMoney());
            parMap.put("incomeMoney", record.getIncomeMoney());
            financeAccountMapper.updateAvailableMoneyByBack(parMap);

            //更改状态为 1
            IncomeRecord parIncome = new IncomeRecord();
            parIncome.setId(record.getId());
            parIncome.setIncomeStatus(1);
            incomeRecordMapper.updateByPrimaryKeySelective(parIncome);
        }


    }
}
