package com.bjpowernode.p2p.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static Date getDateByAddDays(Date date,int days){

        //处理日期类的对象
        Calendar calendar = Calendar.getInstance();

        //将处理的时间设置为参数的日期
        calendar.setTime(date);

        //将时间加上天数
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date getDateByAddMonths(Date date,int months){

        //处理日期类的对象
        Calendar calendar = Calendar.getInstance();

        //将处理的时间设置为参数的日期
        calendar.setTime(date);

        //将时间加上天数
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }


    public static String getDateTemp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
