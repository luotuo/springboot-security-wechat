package com.luotuo.config;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuma on 2017/6/5.
 */
public class Global {
    public static final int DEFAULT_PAGE_SIZE=20;//分页使用，每页条数
    public static final int DEFAULT_PAGE_NUM=0;//分页使用，第几页


    public static final String wechatAppId = "yourwechatappid";
    public static final String wechatSecretKey = "yourwechatsecret";

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
