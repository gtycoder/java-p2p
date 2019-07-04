package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.ResultVO;

public interface UserService {
    long queryAllUserCount();

    User queryUserByPhone(String phone);

    ResultVO Register(String phone, String password);

    int modifyUserById(User paramUser);

    User login(User paramUser);
}
