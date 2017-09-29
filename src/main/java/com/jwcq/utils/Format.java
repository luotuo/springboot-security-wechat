package com.jwcq.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuma on 2017/7/6.
 */
public class Format {

    /**
     * @Function 对其函数，不够位数的在前面补0，保留num的长度位数字
     * @param code
     * @param  num 对其长度
     * @return
     */
    public static String formatCode(long code, int num) {
        String result = "";
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        result = String.format("%0" + num + "d", code);
        return result;
    }

    public static String formatCode(int code, int num) {
        String result = "";
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        result = String.format("%0" + num + "d", code);
        return result;
    }

    public static String formatDate(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
        return  sdf.format(date);
    }

    public static Date formatDateTime(String date) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
        Date o=new Date();
        try{
           o=sdf.parse(date);
        }catch (ParseException e){
           System.out.print("Date error"+e);
       }
        return o;
    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    public static String getNowDateString() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static Date formatDate(String date) {
        if(StringUtils.isBlank(date))return null;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
        Date o=new Date();
        try{
            o=sdf.parse(date);
        }catch (ParseException e){
            System.out.print("Date not yyyy-MM-dd format"+e);
            sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
            try{
                o=sdf.parse(date);
            }catch (Exception ee){
                System.out.print("Date not yyyy-MM-dd HH:mm:ss format"+e);
                return null;
            }
        }
        return o;
    }
}
