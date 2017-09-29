package com.jwcq.custom;

import com.jwcq.security.SecurityConfig;
import com.jwcq.service.PrivilegeConfigService;
import com.jwcq.service.URIResourceService;
import com.jwcq.service.UserPrivilegeService;
import com.jwcq.service.UserService;
import com.jwcq.user.entity.PrivilegeConfig;
import com.jwcq.user.entity.User;

import com.jwcq.user.entity.UserPrivilege;
import com.jwcq.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by luotuo on 17-6-26.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private URIResourceService uriResourceService;
    @Autowired
    private UserPrivilegeService userPrivilegeService;
    @Autowired
    private PrivilegeConfigService privilegeConfigService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("当前的用户名是："+username);
        User user = userService.getUserByLoginName(username);
        if (user == null)
            throw new UsernameNotFoundException("Admin: " + username + "do not exsit!");
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setName(user.getName());
        userInfo.setId(user.getId());
        userInfo.setPassword(user.getPassword());
        Set<AuthorityInfo> authorities = new HashSet<AuthorityInfo>();
        System.out.println("userInfo.getId == " + userInfo.getId());
        if (userInfo.getId() == 1) {
            // Admin, add all privileges
            List<String> uris = uriResourceService.getAll();
            for (String uri : uris) {
                AuthorityInfo authorityInfo = new AuthorityInfo(uri);
                authorities.add(authorityInfo);
            }
        } else {
            // Other users. Find user's privileges and add them
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
        }
        userInfo.setAuthorities(authorities);
        return userInfo;
    }
}
