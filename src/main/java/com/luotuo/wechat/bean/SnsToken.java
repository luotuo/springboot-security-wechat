package com.luotuo.wechat.bean;

import lombok.Data;

/**
 * Created by luotuo on 17-9-22.
 */
@Data
public class SnsToken extends BaseResult {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;
}
