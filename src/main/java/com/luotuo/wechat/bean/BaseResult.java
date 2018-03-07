package com.luotuo.wechat.bean;

import lombok.Data;

/**
 * 微信请求状态数据
 *
 * @author LiYi
 */
@Data
public class BaseResult {
    private static final String SUCCESS_CODE = "0";
    private String errcode;
    private String errmsg;

    public boolean isSuccess() {
        return errcode == null || errcode.isEmpty() || errcode.equals(SUCCESS_CODE);
    }

}
