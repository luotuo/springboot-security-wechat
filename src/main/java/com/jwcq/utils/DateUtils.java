package com.jwcq.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liuma on 2017/7/31.
 */
public class DateUtils {

    /**
     * 计算两个Date之间的工作日时间差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return int 返回两天之间的工作日时间
     */
    public static int countDutyday(Date start, Date end) {
        if (start == null || end == null) return 0;
        if (start.after(end)) return 0;
        Calendar c_start = Calendar.getInstance();
        Calendar c_end = Calendar.getInstance();
        c_start.setTime(start);
        c_end.setTime(end);
        //时分秒毫秒清零
        c_start.set(Calendar.HOUR_OF_DAY, 0);
        c_start.set(Calendar.MINUTE, 0);
        c_start.set(Calendar.SECOND, 0);
        c_start.set(Calendar.MILLISECOND, 0);
        c_end.set(Calendar.HOUR_OF_DAY, 0);
        c_end.set(Calendar.MINUTE, 0);
        c_end.set(Calendar.SECOND, 0);
        c_end.set(Calendar.MILLISECOND, 0);
        //初始化第二个日期，这里的天数可以随便的设置
        int dutyDay = 0;
        while (c_start.compareTo(c_end) < 0) {
            if (c_start.get(Calendar.DAY_OF_WEEK) != 1 && c_start.get(Calendar.DAY_OF_WEEK) != 7)
                dutyDay++;
            c_start.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dutyDay;
    }

    /**
     * 计算两个Date之间的工作日时间差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return int 返回两天之间的节假日时间
     */
    public static int countHoliday(Date start, Date end) {
        if (start == null || end == null) return 0;
        if (start.after(end)) return 0;
        Calendar c_start = Calendar.getInstance();
        Calendar c_end = Calendar.getInstance();
        c_start.setTime(start);
        c_end.setTime(end);
        //初始化第二个日期，这里的天数可以随便的设置
        int holiday = 0;
        while (start.compareTo(end) <= 0) {
            if (c_start.get(Calendar.DAY_OF_WEEK) == 1 && c_start.get(Calendar.DAY_OF_WEEK) == 7)
                holiday++;
            c_start.add(Calendar.DAY_OF_YEAR, 1);
        }
        return holiday;
    }

    /**
     * 获取某天0.0.0时间
     * */
    public static Date getStartDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
       return calendar.getTime();
    }
    public static Date getEndDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        return calendar.getTime();
    }

    /**
     * 获取服务器时间
     */
    public static Date getCurrentDate() {
        Date date = new Date();
        return date;
    }

    /**
     * 获取当年
     */
    public static String getYear() {
        return new SimpleDateFormat("yy", Locale.CHINESE).format(new Date());
    }


    public static String timeCalculate(Date start,Date end){
        if(start==null)return "--";
        if(end==null)end=getCurrentDate();
        Long minus=end.getTime()-start.getTime();
        if(minus<0)return "-1";
        Long day=minus/(3600*24*1000);
        Long hour=(minus-day*(3600*1000*24))/(3600*1000);
        Long  minutes=(minus-day*(3600*1000*24)-hour*(3600*1000))/(60*1000);
        Long seconds=(minus-day*(3600*1000*24)-hour*(3600*1000)-minutes*60*1000)/1000;
        if(day>0)return day+"天 "+hour+"小时"+minutes+"分";
        else return hour+"小时"+minutes+"分";
    }

    public static void main(String[]args){
        String one=timeCalculate(null,null);
        String two=timeCalculate(new Date(1502619414000L),new Date(1502619414000L));
        String three=timeCalculate(new Date(1502619414000L),new Date(1502619414000L));
        String four=timeCalculate(new Date(1187259414000L),null);
        String five=timeCalculate(new Date(1187259414000L),new Date(1502792214000L));

    }
}