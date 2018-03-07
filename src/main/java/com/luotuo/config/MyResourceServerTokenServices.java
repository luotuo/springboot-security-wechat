package com.luotuo.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * Created by luotuo on 17-9-28.
 */
public interface MyResourceServerTokenServices extends ResourceServerTokenServices {
    OAuth2Authentication loadAuthentication(String var1, String var2) throws AuthenticationException, InvalidTokenException;
}
