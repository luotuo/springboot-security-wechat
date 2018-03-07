package com.luotuo.wechat.bean;

import lombok.Data;

/**
 * Created by luotuo on 17-9-22.
 */
@Data
public class Jscode2sessionResult extends BaseResult {
    private String openid;
    private String session_key;
    private Integer expires_in;
}
