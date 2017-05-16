package com.mingyizhudao.qa.util;

import com.mingyizhudao.qa.common.KB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ttshmily on 4/5/2017.
 */
public class UT {

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static String randomString(int length) {
        StringBuffer sb = new StringBuffer();
        String string = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = string.length();
        for (int i = 0; i < length; i++) {
            sb.append(string.charAt((int)randomInt(len-1)));
        }
        return sb.toString();
    }

    public static long randomInt(long count) {
        return (long) Math.floor(Math.random() * count);
    }

    public static String randomDateFromNow(int i, int j) {
        try {
            Date now = df.parse(df.format(new Date()));
            Calendar date = Calendar.getInstance();
            date.setTime(now);

            date.set(Calendar.DATE, date.get(Calendar.DATE) + i);
            Date start = df.parse(df.format(date.getTime()));// 构造开始日期
            date.set(Calendar.DATE, date.get(Calendar.DATE) + j);
            Date end = df.parse(df.format(date.getTime()));// 构造结束日期

            if (start.getTime() > end.getTime()) {
                return null;
            }
            long mili = start.getTime() + (long) (Math.random() * (end.getTime() - start.getTime()));
            return df.format(new Date(mili));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String randomDate(String startDate, String endDate) {
        try {
            Date start = df.parse(startDate);// 构造开始日期
            Date end = df.parse(endDate);// 构造结束日期
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = start.getTime() + (long) (Math.random() * (end.getTime() - start.getTime()));
            return df.format(new Date(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String randomDateTillNow() {
        try {
            Date start = df.parse("2017-03-13");// 构造开始日期
            Date end = df.parse(df.format(new Date()));// 构造结束日期
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = start.getTime() + (long) (Math.random() * (end.getTime() - start.getTime()));
            return df.format(new Date(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String randomKey(HashMap<String, String> map) {
        String[] keys = map.keySet().toArray(new String[] {});
        return keys[(int)randomInt(keys.length-1)];
    }

    public static String randomPhone() {
        Random random = new Random();
        Integer m = random.nextInt(99999);
        return "13" + String.format("%05d", m) + "9999";
    }

//    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) {
//            System.out.println(randomKey(KB.kb_major));
//        }
//    }
}
