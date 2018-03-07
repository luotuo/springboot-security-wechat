package com.luotuo.custom;


import org.springframework.security.core.GrantedAuthority;

/**
 * Created by luotuo on 17-6-26.
 */
public class AuthorityInfo implements GrantedAuthority {
    private static final long serialVersionUID = -175781100474818800L;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 权限URL
     */
    private String url;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    private String method;

    public AuthorityInfo(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public AuthorityInfo(String url) {
        this.url = url;
        this.method = "";
    }

    @Override
    public String getAuthority() {
        return this.url + ";" + this.method;
    }

    public void setAuthority(String url, String method) {
        this.url = url;
        this.method = method;
    }

}
