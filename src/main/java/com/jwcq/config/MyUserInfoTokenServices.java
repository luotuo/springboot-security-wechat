package com.jwcq.config;

import com.jwcq.custom.AuthorityInfo;
import com.jwcq.service.PrivilegeConfigService;
import com.jwcq.service.UserPrivilegeService;
import com.jwcq.service.UserService;
import com.jwcq.service.UserWechatService;
import com.jwcq.user.entity.PrivilegeConfig;
import com.jwcq.user.entity.User;
import com.jwcq.user.entity.UserPrivilege;
import com.jwcq.user.entity.UserWechat;
import com.jwcq.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedPrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by luotuo on 17-9-27.
 */
public class MyUserInfoTokenServices implements MyResourceServerTokenServices {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final String userInfoEndpointUrl;
    private final String clientId;
    private OAuth2RestOperations restTemplate;
    private String tokenType = "Bearer";
    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();
    private PrincipalExtractor principalExtractor = new FixedPrincipalExtractor();


    private UserService userService;
    private UserWechatService userWechatService;
    private UserPrivilegeService userPrivilegeService;
    private PrivilegeConfigService privilegeConfigService;

    public MyUserInfoTokenServices(String userInfoEndpointUrl,
                                   String clientId,
                                   UserService userService,
                                   UserWechatService userWechatService,
                                   UserPrivilegeService userPrivilegeService,
                                   PrivilegeConfigService privilegeConfigService) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        this.clientId = clientId;
        this.userService = userService;
        this.userWechatService = userWechatService;
        this.userPrivilegeService = userPrivilegeService;
        this.privilegeConfigService = privilegeConfigService;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setRestTemplate(OAuth2RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setAuthoritiesExtractor(AuthoritiesExtractor authoritiesExtractor) {
        Assert.notNull(authoritiesExtractor, "AuthoritiesExtractor must not be null");
        this.authoritiesExtractor = authoritiesExtractor;
    }

    public void setPrincipalExtractor(PrincipalExtractor principalExtractor) {
        Assert.notNull(principalExtractor, "PrincipalExtractor must not be null");
        this.principalExtractor = principalExtractor;
    }

    public OAuth2Authentication loadAuthentication(String accessToken,
                                                   String ip) throws AuthenticationException, InvalidTokenException {
        Map<String, Object> map = this.getMap(this.userInfoEndpointUrl, accessToken);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println("key == " + entry.getKey() + " value == " + entry.getValue());
        }
        if(map.containsKey("error")) {
            if(this.logger.isDebugEnabled()) {
                this.logger.debug("userinfo returned error: " + map.get("error"));
            }

            throw new InvalidTokenException(accessToken);
        } else {
            return this.extractAuthentication(map, ip);
        }
    }

    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        return this.loadAuthentication(accessToken, null);
    }

    private OAuth2Authentication extractAuthentication(Map<String, Object> map, String ip) {
        // Here, we try to get user's privileges
        // First, try to find user by openid
        // if no user was found, try to find user in user wechat repository,
        // if now userwechat was found, save it, then add it into user repository
        // TODO: need basic privileges
        User user = null;
        UserWechat userWechat = null;
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (map.containsKey("openid")) {
            String openId = (String)map.get("openid");
            System.out.println("openId == " + openId);
            user = userService.getUserByOpenId(openId);
            if (user == null) {
                // Try to find userwechat
                userWechat = userWechatService.getByOpenId(openId);
                if (userWechat == null) {
                    userWechat = new UserWechat(map);
                    userWechat = userWechatService.save(userWechat);
                    user = new User(userWechat);
                    user.setHas_join(0);
                }
            }
            user.setLast_ip(ip);
            user = userService.save(user);
            System.out.println("user name == " + user.getId());
            map.put("username", user.getId());
            List<UserPrivilege> userPrivileges = userPrivilegeService.findByUserId(user.getId());
            List<Long> privilegeIds = new ArrayList<>();
            for (UserPrivilege p : userPrivileges) {
                privilegeIds.add(p.getPrivilege_id());
            }
            if (!privilegeIds.isEmpty()) {
                List<PrivilegeConfig> privilegeConfigs = privilegeConfigService.getByIds(privilegeIds);
                for (PrivilegeConfig p : privilegeConfigs) {
                    if (StringUtils.isNotBlank(p.getUrl())) {
                        AuthorityInfo authorityInfo = new AuthorityInfo(p.getUrl());
                        authorities.add(authorityInfo);
                    }
                }
            }
        } else {
            authorities = this.authoritiesExtractor.extractAuthorities(map);
        }
        Object principal = this.getPrincipal(map);
        if (authorities != null)
            System.out.println("authorities == " + authorities.size());
        OAuth2Request request = new OAuth2Request((Map)null,
                this.clientId,
                (Collection)null,
                true,
                (Set)null, (Set)null, (String)null, (Set)null, (Map)null);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        token.setDetails(map);
        return new OAuth2Authentication(request, token);
    }

    private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
        return this.extractAuthentication(map, null);
    }

    protected Object getPrincipal(Map<String, Object> map) {
        Object principal = this.principalExtractor.extractPrincipal(map);
        return principal == null?"unknown":principal;
    }

    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    private Map<String, Object> getMap(String path, String accessToken) {
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Getting user info from: " + path);
        }
        try {
            OAuth2RestOperations restTemplate = this.restTemplate;
            if(restTemplate == null) {
                BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
                resource.setClientId(this.clientId);
                restTemplate = new OAuth2RestTemplate(resource);
            }
            OAuth2AccessToken existingToken = ((OAuth2RestOperations)restTemplate).getOAuth2ClientContext().getAccessToken();
            if(existingToken == null || !accessToken.equals(existingToken.getValue())) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessToken);
                token.setTokenType(this.tokenType);
                ((OAuth2RestOperations)restTemplate).getOAuth2ClientContext().setAccessToken(token);
            }

            return (Map)((OAuth2RestOperations)restTemplate).getForEntity(path, Map.class, new Object[0]).getBody();
        } catch (Exception var6) {
            this.logger.warn("Could not fetch user details: " + var6.getClass() + ", " + var6.getMessage());
            return Collections.singletonMap("error", "Could not fetch user details");
        }
    }
}

