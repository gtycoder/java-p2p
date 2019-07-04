package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.LoanInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoanInfoServiceImpl implements LoanInfoService {
    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public List<LoanInfo> queryLoanListBy(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanByProductType(paramMap);
    }

    @Override
    public Double queryHistoryAvgRate() {

        //从redis中取出数据
        BoundValueOperations<String, Object> boundValueOps = redisTemplate.boundValueOps(Constants.HISTORY_AVG_RATE);
        Double avgRate= (Double) boundValueOps.get();
        if (null==avgRate) {
            //从数据库取出
            avgRate=loanInfoMapper.selectAvgRate();
            //放入redis中
            boundValueOps.set(avgRate, 20, TimeUnit.MINUTES);
        }
        System.out.println(avgRate);

        return avgRate;
    }
}
