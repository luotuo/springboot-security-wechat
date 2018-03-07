package com.luotuo.wechat.sns;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.luotuo.wechat.API;
import com.luotuo.wechat.bean.Jscode2sessionResult;
import com.luotuo.wechat.bean.SnsToken;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import com.luotuo.wechat.bean.BaseResult;
import com.luotuo.wechat.LocalHttpClient;
import com.luotuo.user.entity.UserWechat;

/**
 * 网页授权
 *
 * @author LiYi
 */
public class SnsAPI extends BaseAPI {

    /**
     * 通过code换取网页授权access_token
     *
     * @param appid  appid
     * @param secret secret
     * @param code   code
     * @return SnsToken
     */
    public static SnsToken oauth2AccessToken(String appid, String secret, String code) {
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setUri(BASE_URI + "/sns/oauth2/access_token")
                .addParameter("appid", appid)
                .addParameter("secret", secret)
                .addParameter("code", code)
                .addParameter("grant_type", "authorization_code")
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, SnsToken.class);
    }

    /**
     * 通过code换取网页授权access_token (第三方平台开发)
     *
     * @param appid                  appid
     * @param code                   code
     * @param component_appid        服务开发方的appid
     * @param component_access_token 服务开发方的access_token
     * @return SnsToken
     */
    public static SnsToken oauth2ComponentAccessToken(String appid, String code, String component_appid, String component_access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setUri(BASE_URI + "/sns/oauth2/component/access_token")
                .addParameter("appid", appid)
                .addParameter("code", code)
                .addParameter("grant_type", "authorization_code")
                .addParameter("component_appid", component_appid)
                .addParameter("component_access_token", component_access_token)
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, SnsToken.class);
    }


    /**
     * 刷新access_token
     *
     * @param appid         appid
     * @param refresh_token refresh_token
     * @return SnsToken
     */
    public static SnsToken oauth2RefreshToken(String appid, String refresh_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setUri(BASE_URI + "/sns/oauth2/refresh_token")
                .addParameter("appid", appid)
                .addParameter("refresh_token", refresh_token)
                .addParameter("grant_type", "refresh_token")
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, SnsToken.class);
    }

    /**
     * 刷新access_token (第三方平台开发)
     *
     * @param appid                  appid
     * @param refresh_token          refresh_token
     * @param component_appid        服务开发商的appid
     * @param component_access_token 服务开发方的access_token
     * @return SnsToken
     */
    public static SnsToken oauth2ComponentRefreshToken(String appid, String refresh_token, String component_appid, String component_access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setUri(BASE_URI + "/sns/oauth2/component/refresh_token")
                .addParameter("appid", appid)
                .addParameter("refresh_token", refresh_token)
                .addParameter("grant_type", "refresh_token")
                .addParameter("component_appid", component_appid)
                .addParameter("component_access_token", component_access_token)
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, SnsToken.class);
    }


    /**
     * 检验授权凭证（access_token）是否有效
     *
     * @param access_token access_token
     * @param openid       openid
     * @return result
     * @since 2.8.1
     */
    public static BaseResult auth(String access_token, String openid) {
        HttpUriRequest httpUriRequest = RequestBuilder.get()
                .setUri(BASE_URI + "/sns/auth")
                .addParameter(PARAM_ACCESS_TOKEN, API.accessToken(access_token))
                .addParameter("openid", openid)
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }


    /**
     * 拉取用户信息(需scope为 snsapi_userinfo)
     *
     * @param access_token access_token
     * @param openid       openid
     * @param lang         国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * @param emoji        表情解析方式<br>
     *                     0 		  不设置 <br>
     *                     1 HtmlHex 格式<br>
     *                     2 HtmlTag 格式<br>
     *                     3 Alias  格式<br>
     *                     4 HtmlDec 格式<br>
     *                     5 PureText 纯文本<br>
     * @return User
     * @since 2.7.1
     */
    public static UserWechat userinfo(String access_token, String openid, String lang, int emoji) {
        HttpUriRequest httpUriRequest = RequestBuilder.get()
                .setUri(BASE_URI + "/sns/userinfo")
                .addParameter(PARAM_ACCESS_TOKEN, access_token)
                .addParameter("openid", openid)
                .addParameter("lang", lang)
                .build();
        UserWechat user = LocalHttpClient.executeJsonResult(httpUriRequest, UserWechat.class);
        return user;
    }

    /**
     * 拉取用户信息(需scope为 snsapi_userinfo)
     *
     * @param access_token access_token
     * @param openid       openid
     * @param lang         国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * @return UserWechat
     */
    public static UserWechat userinfo(String access_token, String openid, String lang) {
        return userinfo(access_token, openid, lang, 0);
    }

    /**
     * 生成网页授权 URL
     *
     * @param appid           appid
     * @param redirect_uri    自动URLEncoder
     * @param snsapi_userinfo snsapi_userinfo
     * @param state           可以为空
     * @return url
     */
    public static String connectOauth2Authorize(String appid, String redirect_uri, boolean snsapi_userinfo, String state) {
        return connectOauth2Authorize(appid, redirect_uri, snsapi_userinfo, state, null);
    }

    /**
     * 生成网页授权 URL  (第三方平台开发)
     *
     * @param appid           appid
     * @param redirect_uri    自动URLEncoder
     * @param snsapi_userinfo snsapi_userinfo
     * @param state           可以为空
     * @param component_appid 第三方平台开发，可以为空。
     *                        服务方的appid，在申请创建公众号服务成功后，可在公众号服务详情页找到
     * @return url
     */
    public static String connectOauth2Authorize(String appid, String redirect_uri, boolean snsapi_userinfo, String state, String component_appid) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OPEN_URI + "/connect/oauth2/authorize?")
                    .append("appid=").append(appid)
                    .append("&redirect_uri=").append(URLEncoder.encode(redirect_uri, "utf-8"))
                    .append("&response_type=code")
                    .append("&scope=").append(snsapi_userinfo ? "snsapi_userinfo" : "snsapi_base")
                    .append("&state=").append(state == null ? "" : state);
            if (component_appid != null) {
                sb.append("&component_appid=").append(component_appid);
            }
            sb.append("#wechat_redirect");
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成网页授权 URL  (网站应用微信登录)
     * 2.5.3
     *
     * @param appid        appid
     * @param redirect_uri 自动URLEncoder
     * @param state        可以为空
     * @return url
     */
    public static String connectQrconnect(String appid, String redirect_uri, String state) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(OPEN_URI + "/connect/qrconnect?")
                    .append("appid=").append(appid)
                    .append("&redirect_uri=").append(URLEncoder.encode(redirect_uri, "utf-8"))
                    .append("&response_type=code")
                    .append("&scope=snsapi_login")
                    .append("&state=").append(state == null ? "" : state)
                    .append("#wechat_redirect");
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * code 换取 session_key（微信小程序）
     *
     * @param appid   appid
     * @param secret  secret
     * @param js_code js_code
     * @return result
     * @since 2.8.3
     */
    public static Jscode2sessionResult jscode2session(String appid, String secret, String js_code) {
        HttpUriRequest httpUriRequest = RequestBuilder.get()
                .setUri(BASE_URI + "/sns/jscode2session")
                .addParameter("appid", appid)
                .addParameter("secret", secret)
                .addParameter("js_code", js_code)
                .addParameter("grant_type", "authorization_code")
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Jscode2sessionResult.class);
    }

    /**
     * code 换取 session_key（微信小程序   第三方平台）
     *
     * @param appid                  appid
     * @param js_code                js_code
     * @param component_appid        component_appid
     * @param component_access_token component_access_token
     * @return result
     * @since 2.8.9
     */
    public static Jscode2sessionResult componentJscode2session(String appid, String js_code, String component_appid, String component_access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.get()
                .setUri(BASE_URI + "/sns/component/jscode2session")
                .addParameter("appid", appid)
                .addParameter("js_code", js_code)
                .addParameter("grant_type", "authorization_code")
                .addParameter("component_appid", component_appid)
                .addParameter("component_access_token", API.componentAccessToken(component_access_token))
                .build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Jscode2sessionResult.class);
    }


}
