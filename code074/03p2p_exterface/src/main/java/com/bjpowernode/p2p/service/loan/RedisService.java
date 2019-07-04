package com.bjpowernode.p2p.service.loan;

public interface RedisService {
    void set(String key, String value, int time);


    String get(String key);

    String getOnlyNum();
}
