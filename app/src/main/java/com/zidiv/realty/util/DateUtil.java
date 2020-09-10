package com.zidiv.realty.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/25.
 */
public class DateUtil {

    public static String getNowTime(String fmt) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }
}
