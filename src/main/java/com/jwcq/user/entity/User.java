package com.jwcq.user.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by luotuo on 17-6-29.
 */
@Entity
@Table(name = "user")
@Data
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name="测试账户";
    private String department;
    private String phone;
    private String email;
    private int state;
    private String state_str;
    private String icons;
    private String password;
    private String wechat;
    private String last_ip;
    private int has_login = 0;
    private String resume;//用户简介
    private String title; //用户头衔
    private String city;  //用户所在地 【新增】
    private long user_wechat_id;
    private String wechat_open_id;
    private int bind_wechat = 0;
    private String wechat_headimgurl;
    private int has_join = 1;

    public User() {}
    public User(UserWechat userWechat) {
        this.wechat_open_id = userWechat.getOpenid();
        this.user_wechat_id = userWechat.getId();
        this.password = "3audit-jwcq-1234567654321";
        this.state = 1;
        this.state_str = "正常";
        this.wechat_headimgurl = userWechat.getHeadimgurl();
        this.bind_wechat = 1;
    }

    public void bindWechat(UserWechat userWechat) {
        this.user_wechat_id = userWechat.getId();
        this.wechat_headimgurl = userWechat.getHeadimgurl();
        this.wechat_open_id = userWechat.getOpenid();
        this.bind_wechat = 1;
    }
}
