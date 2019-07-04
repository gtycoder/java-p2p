package com.bjpowernode.p2p;

import com.bjpowernode.p2p.service.loan.BidInfoService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyTest {
    public static void main(String[] args) {
        //获取spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        //获取指定对象
        BidInfoService infoServiceImpl = (BidInfoService) context.getBean("bidInfoServiceImpl");
        //使用jdk的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        //准备参数
        Map<String,Object> retMap=new HashMap<>();
        retMap.put("uid", 1);
        retMap.put("loanId", 3);
        retMap.put("bidMoney", 1.0);
        System.out.println("开始");
        for (int i = 0; i < 1000; i++) {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return infoServiceImpl.invest(retMap);
                }
            });

        }
        executorService.shutdown();
        System.out.println("结束");

    }
}
