package com.mesasoft.cn.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author yjy
 * @version 1.0
 * @date 2021/2/25 11:26 上午
 */
public class TimeUtils {
    public static final Long HOUR_TO_MILLISECONDS = 3600000L;
    public static final Long DAY_TO_MILLSEDONDS = 86400000L;
    public static final Integer SECOND_TO_MILLSEDONDS = 1000;


    /**
     * 获得当前时间小时的起始（0分钟）时间
     */
    public static Date getStartOfHour() {
        return getStartOfHour(0);
    }
    public static Date getStartOfHour(Integer offset) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.HOUR, offset);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        return ca.getTime();
    }

    /**
     * 获得当前日期的起始（0时）时间
     */
    public static Date getStartOfDay() {
        return getStartOfDay(0);
    }
    public static Date getStartOfDay(Integer bias) {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, bias);
        ca.set(Calendar.HOUR, -12);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        return ca.getTime();
    }

    public static Date getStartOfMonth() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DATE, 1);
        ca.set(Calendar.HOUR, -12);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        return ca.getTime();
    }

}

