package com.dcits.modelBank.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 2017-11-07 09:33.
 *
 * @author kevin
 */
public class DateUtil {
    /**
     * 将时间转化为时间戳
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * 将时间戳转化为时间
     *
     * @param s
     * @return
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static void main(String[] args) throws ParseException {

        String time = "2017-11-07 00:00:00";
        String timestamp = dateToStamp(time);
        time = "2017-11-07 09:00:00";
        String timeStamp2 = dateToStamp(time);
        long stamp1 = Long.valueOf(timestamp);
        long stamp2 = Long.valueOf(timeStamp2);
        if (stamp1 < stamp2) {
            System.out.println(time);
        }
        System.out.println(timestamp);
    }
}
