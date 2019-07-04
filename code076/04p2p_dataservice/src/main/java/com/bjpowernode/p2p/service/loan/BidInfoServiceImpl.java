package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.service.BidInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Double querySumBidMoney() {
        BoundValueOperations boundValueOps = redisTemplate.boundValueOps(Constants.SUM_BID_MONEY);
        Double sumBidMoney = (Double) boundValueOps.get();
        if (null==sumBidMoney) {
            sumBidMoney=bidInfoMapper.selectSumBidMoney();
            boundValueOps.set(sumBidMoney, 20, TimeUnit.MINUTES);
        }
        return sumBidMoney;
    }
}
