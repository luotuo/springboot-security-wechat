package com.jwcq.user.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;

/**
 * Created by luotuo on 17-9-21.
 */
@Entity
@Table(name = "user_wechat")
@Data
public class UserWechat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Integer subscribe;		//用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
    private String openid;			//用户的标识，对当前公众号唯一
    private String nickname;
    //private String nickname_emoji;	//昵称 表情转义
    private String sex;			//用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private Integer subscribe_time;
    private String[] privilege;		//sns 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
    private String unionid;			//多个公众号之间用户帐号互通UnionID机制
    private Integer groupid;
    private String remark;			//公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注
    private Integer[] tagid_list;	//用户被打上的标签ID列表
    public UserWechat() {}
    public UserWechat(Map<String, Object> attrs) {
        this.unionid = attrs.get("unionid") == null ? "" : (String) attrs.get("unionid");
        this.city = attrs.get("city") == null ? "" : (String) attrs.get("city");
        this.country = attrs.get("country") == null ? "" : (String) attrs.get("country");
        this.headimgurl = attrs.get("headimgurl") == null ? "" : (String) attrs.get("headimgurl");
        this.nickname = attrs.get("nickname") == null ? "" : (String) attrs.get("nickname");
        this.openid = attrs.get("openid") == null ? "" : (String) attrs.get("openid");
        this.province = attrs.get("province") == null ? "" : (String)attrs.get("province");
        this.sex = attrs.get("sex") == null ? "" : attrs.get("sex").toString();
        if (this.sex.equals("1")) this.sex = "男";
        else if (this.sex.equals("2")) this.sex = "女";
    }
}
