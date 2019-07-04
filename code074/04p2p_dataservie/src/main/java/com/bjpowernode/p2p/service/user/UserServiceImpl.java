package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public long queryAllUserCount() {
        //从redis中获取数据
        /*ValueOperations<String, Object> value = redisTemplate.opsForValue();
        Long count = (Long) value.get(Constants.ALL_USERS_COUNT);*/
        BoundValueOperations<String, Object> boundValue = redisTemplate.boundValueOps(Constants.ALL_USERS_COUNT);
        Long count = (Long) boundValue.get();
        if (null == count) {
            //从数据库中取
            count = userMapper.selectAllUserCount();
            //放入redis中
            //value.set(Constants.ALL_USERS_COUNT, count, 15, TimeUnit.MINUTES);
            boundValue.set(count, 15, TimeUnit.MINUTES);

        }
        return count;
    }

    @Override
    public User login(User paramUser) {
        //查询有木有
        User user = userMapper.selectUserByPhoneAndPassword(paramUser);
        //有就更新
        if (null != user) {
            //有,更新时间
            paramUser.setLastLoginTime(new Date());
            userMapper.updateByPrimaryKeySelective(paramUser);
        }
        //没有返回
        return user;
    }

    @Override
    public int modifyUserById(User paramUser) {
        return userMapper.updateByPrimaryKeySelective(paramUser);
    }

    @Override
    public ResultVO Register(String phone, String password) {
        //两件事情
        ResultVO result = new ResultVO();
        //注册新用户
        User user = new User();
        user.setPhone(phone);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());
        user.setLoginPassword(password);
        int i = userMapper.insertSelective(user);
        if (i != 1) {
            result.setShowCode(Constants.FAIL);
            return result;
        } else {
            //开立金融账户
            User user1 = userMapper.selectUserByPhone(phone);
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(user1.getId());
            financeAccount.setAvailableMoney(888.0);
            int i1 = financeAccountMapper.insertSelective(financeAccount);
            if (i1 != 1) {
                result.setShowCode(Constants.FAIL);
                return result;
            } else {
                result.setShowCode(Constants.SUCCESS);
            }
        }
        //开立新账户
        return result;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }
}
