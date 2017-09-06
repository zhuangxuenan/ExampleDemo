package com.xuenan.example.commonutil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间比较工具类
 *
 * @author xuenan
 */
public class TimeCompare {

    /**
     * 小时
     */
    private static final int hour = 1000 * 60 * 60;

    /**
     * 支持 yyyy-MM-dd HH精确到小时
     *
     * @param start 开始时间 if：2012-2-10 11
     * @param end   结束时间  if:2012-02-11 13:12:21
     * @return 小时数
     */
    public static int compareHourTime(String start, String end) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date d1 = df.parse(end);
            Date d2 = df.parse(start);
            long diff = d1.getTime() - d2.getTime();
            int hours = (int) (diff / hour);
            return hours;
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 根据当前时间 获取时间差 支持 yyyy-MM-dd HH精确到小时
     *
     * @param start 开始时间 if：2012-2-10 11
     * @return 小时数
     */
    public static int compareHourTime(String start) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d2 = sdf.parse(start);
            long diff = System.currentTimeMillis() - d2.getTime();
            int hours = (int) (diff / hour);
            return hours;
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 用于日期的加减
     * 获取指定日后 后 dayAddNum 天的 日期
     *
     * @param day       日期，格式为String："2013-9-3";
     * @param dayAddNum 增加天数 格式为int;
     * @return
     */
    public static String getDateStr(String day, int dayAddNum) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date newDate2 = new Date(nowDate.getTime() + dayAddNum * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

    /***
     * 日期月份减一个月
     *
     * @param datetime 日期(2014-11)
     * @return 2014-10
     */
    public static String dateFormat(String datetime, int change_Month) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.MONTH, change_Month);
        date = cl.getTime();
        return sdf.format(date);
    }

    /***
     * 季度的加减
     *
     * @param datetime     当前日期
     * @param change_Month 3的倍数
     *                     日期(2014-4)
     * @return 2014-3
     */
    public static String quarterFormat(String datetime, int change_Month) {
        String dateMonth = dateFormat(datetime, change_Month);
        String[] ss = dateMonth.split("-");
        String year = ss[0];
        //季度
        int quarter;
        int month = Integer.parseInt(ss[1]);
        if (month % 3 == 0) {
            quarter = month / 3;
        } else {
            quarter = (month / 3) + 1;
        }
        return year + "^" + quarter;
    }

    /****
     * 传入具体日期 ，返回具体日期减一个月。
     *
     * @param date 日期(2014-04-20)
     * @return 2014-03-20
     * @throws ParseException
     */
    public static String subMonth(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = sdf.parse(date);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);

        rightNow.add(Calendar.MONTH, -1);
        Date dt1 = rightNow.getTime();
        String reStr = sdf.format(dt1);

        return reStr;
    }

    /**
     * 传入一个年对年进行加减运算
     */
    public static String yearFormat(String year, int change_year) {
        int currentYear = Integer.parseInt(year);
        int requareYear = currentYear + change_year;
        return String.valueOf(requareYear);
    }
}