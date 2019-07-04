package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService{
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void set(String key, String value, int time) {
        BoundValueOperations<String, Object> boundValueOps = redisTemplate.boundValueOps(key);
        //System.out.println("++++++"+value);
        boundValueOps.set(value, time, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String value = (String) opsForValue.get(key);
        return value;
    }

    @Override
    public String getOnlyNum() {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Long increment = opsForValue.increment(Constants.ONLY_NUM, 1);
        return increment.toString();
    }
}
