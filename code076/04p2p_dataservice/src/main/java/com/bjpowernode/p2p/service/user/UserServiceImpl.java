package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public Long querySumUserCount() {
        BoundValueOperations<String, Object> boundValueOps = redisTemplate.boundValueOps(Constants.SUM_USER_COUNT);
        Long sumUserCount = (Long) boundValueOps.get();
        if (null==sumUserCount) {
            sumUserCount=userMapper.selectSumUserCount();
            boundValueOps.set(sumUserCount, 20, TimeUnit.MINUTES);
        }
        System.out.println(sumUserCount);
        return sumUserCount;
    }
}
