package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;

import com.bjpowernode.p2p.model.vo.BidMoneyTopVO;
import com.bjpowernode.p2p.model.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service(value = "bidInfoServiceImpl")
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Override
    public Double querySumBidMoney() {
        ValueOperations value = redisTemplate.opsForValue();
        Double sumBidMoney = (Double) value.get(Constants.SUM_BID_MONEY);
        if (null == sumBidMoney) {
            sumBidMoney = bidInfoMapper.selectSumBidMoney();
            value.set(Constants.SUM_BID_MONEY, sumBidMoney, 15, TimeUnit.MINUTES);

        }
        //如果有
        return sumBidMoney;
    }

    @Override
    public List<BidInfo> queryBidInfoListByLoanId(Map<String, Object> bidMap) {
        List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoByLoanId(bidMap);
        return bidInfoList;
    }

    @Override
    public List<BidInfo> queryBidInfoListByUid(Map<String, Object> parMap) {
        return bidInfoMapper.selectBidInfoByUid(parMap);
    }

    @Override
    public ResultVO invest(Map<String, Object> parMap) {
        //将参数转型取出
        Integer uid = (Integer) parMap.get("uid");
        Integer loanId = (Integer) parMap.get("loanId");
        Double bidMoney = (Double) parMap.get("bidMoney");
        String phone = (String) parMap.get("phone");

        ResultVO result = new ResultVO();
        //默认给一个成功
        result.setShowCode(Constants.SUCCESS);
        //更新产品的剩余可投金额
        //可能有多线程超买的现象,使用乐观锁,增加一个版本号进行判断
        //先获取版本号
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        Integer version = loanInfo.getVersion();
        //每次进行减去投资的时候,将version正在原来的基础上加一
        parMap.put("version", version);
        //更新剩余可投在数据库中进行数据的更新
        Integer loanInfoCount = loanInfoMapper.updateLeftProductMoneyByBid(parMap);
        System.out.println("++++++" + loanInfoCount);
        //如果更新失败返回
        if (loanInfoCount != 1) {
            result.setShowCode(Constants.FAIL);
            return result;
        }
        //更新成功后进行
        //账户可用金额
        Integer financeCount = financeAccountMapper.updateAvailableMoneyByBid(parMap);
        if (financeCount != 1) {
            //账户金额更新失败就返回
            result.setShowCode(Constants.FAIL);
            return result;
        }
        //更新成功之后
        //增加投资记录
        BidInfo parBid = new BidInfo();
        parBid.setUid(uid);
        parBid.setLoanId(loanId);
        parBid.setBidTime(new Date());
        parBid.setBidMoney(bidMoney);
        parBid.setBidStatus(1);
        int bidCount = bidInfoMapper.insertSelective(parBid);
        if (bidCount != 1) {
            //增加失败
            result.setShowCode(Constants.FAIL);
            return result;
        }
        //增加成功
        //再次查看产品的剩余可投,更新产品的状态
        LoanInfo loanInfoNew = loanInfoMapper.selectByPrimaryKey(loanId);
        //获取可投数量
        Double leftProductMoney = loanInfoNew.getLeftProductMoney();
        if (0 == leftProductMoney) {
            //满标了改变状态
            LoanInfo parLoanInfo = new LoanInfo();
            parLoanInfo.setId(loanId);
            parLoanInfo.setProductStatus(1);//0是未满标  1是已满标  2满标且生成收益计划
            //记得改变一下满标时间
            parLoanInfo.setProductFullTime(new Date());
            int updateLoanInfoStatus = loanInfoMapper.updateByPrimaryKeySelective(parLoanInfo);
            if (updateLoanInfoStatus != 1) {
                result.setShowCode(Constants.FAIL);
                return result;
            }
            //方法不结束,
        }
        //走到这里就是成功了
        result.setShowCode(Constants.SUCCESS);

        //将投资的纪录放入redis数据库中
        /*这里是要做投资排行榜的功能,需要使用到的数据 1  phone  allbidMoney
        * 是一个有顺序的键值对  使用redis的zset集合
        * */
        ZSetOperations opsForZSet = redisTemplate.opsForZSet();
        opsForZSet.incrementScore(Constants.BIDMONEY_TOP, phone, bidMoney);

        return result;
    }

    @Override
    public List<BidMoneyTopVO> queryBidMoneyTop() {
        List<BidMoneyTopVO> bidMoneyTopVOList=new ArrayList<>();
        //从redis中取出数据按照顺序
        ZSetOperations opsForZSet = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple> set = opsForZSet.reverseRangeWithScores(Constants.BIDMONEY_TOP, 0, 5);
        Iterator<ZSetOperations.TypedTuple> iterator = set.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple next = iterator.next();
            String phone = (String) next.getValue();
            Double score = next.getScore();

            //将值放入对象中,将对象放入list中
            BidMoneyTopVO vo=new BidMoneyTopVO();
            vo.setBidMoney(score);
            vo.setPhone(phone);
            bidMoneyTopVOList.add(vo);
        }
        return bidMoneyTopVOList;
    }
}
