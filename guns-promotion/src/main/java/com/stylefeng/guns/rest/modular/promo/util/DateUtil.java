package com.stylefeng.guns.rest.modular.promo.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date addNow(int month){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }
}
