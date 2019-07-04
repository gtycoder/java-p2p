package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoanInfoServiceImpl implements LoanInfoService{
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Double queryHistoryAvgRate() {
        //改变redis的序列化的方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //先从redis中获取值
        //获取redis操作对象   使用模板对象进行具体类型的对象的创建  redis有5中类型使用opsfor..获取
        ValueOperations value = redisTemplate.opsForValue();
        Double historyAvgRate = (Double) value.get(Constants.HISTORY_AVG_RATE);
        //判断是否有值
        if (null == historyAvgRate) {
            //没有从数据库中获取
            historyAvgRate=loanInfoMapper.selectHistoryAvgRate();
            //然后放到redis中
            value.set(Constants.HISTORY_AVG_RATE, historyAvgRate, 14, TimeUnit.MINUTES);
        }
        //有使用

        return historyAvgRate;
    }

    @Override
    public List<LoanInfo> queryProductListBy(Map<String, Object> map) {
        //从数据库中取出数据
        List<LoanInfo> loanInfoList=loanInfoMapper.selectLoanInfoByProduceType(map);
        return loanInfoList;
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> map) {
        PaginationVO<LoanInfo> pt=new PaginationVO<>();
        //获取总条数
        Long total=loanInfoMapper.selectLoanInfoByPage(map);
        pt.setTotal(total);
        //获取每页的数据
        List<LoanInfo> loanInfoList=loanInfoMapper.selectLoanInfoByProduceType(map);
        pt.setDataList(loanInfoList);
        return pt;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }
}
