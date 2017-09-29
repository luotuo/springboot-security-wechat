package com.jwcq.service;

import com.jwcq.MyExceptions.NullException;
import com.jwcq.config.Global;
import com.jwcq.global.EncryptionAlgs;
import com.jwcq.user.entity.*;
import com.jwcq.user.repository.UserRepository;
import com.jwcq.MyExceptions.AlreadyExistException;

import com.jwcq.utils.StringUtils;
import com.jwcq.wechat.bean.SnsToken;
import com.jwcq.wechat.sns.SnsAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * Created by luotuo on 17-6-30.
 */
@Service
@Transactional("secondTransactionManager")
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPrivilegeService userPrivilegeService;

    @Autowired
    private PrivilegeConfigService privilegeConfigService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePrivilegeService rolePrivilegeService;

    @Autowired
    private UserWechatService userWechatService;

    public User getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByLoginName(String loginName) {
        return userRepository.getUserByLoginName(loginName);
    }

    public User getUserByOpenId(String openId) {
        return userRepository.getUserByOpenId(openId);
    }

    public UserResponse save(HttpServletRequest request) throws AlreadyExistException, NullException {
        User user = new User();
        String phone = request.getParameter("phone");
        String name = request.getParameter("name");
        if (phone.equals("") || name.equals(""))
            throw new NullException("手机号和用户姓名不能为空");
        User temp = userRepository.findUserByPhone(phone);
        if (temp != null)
            throw new AlreadyExistException("用户编号已存在");
        user.setPhone(phone);
        user.setName(name);
        String department = request.getParameter("department");
        String email = request.getParameter("email");
        //String password = EncryptionAlgs.getMD5(request.getParameter("password"));
        String password = request.getParameter("password");
//        if (request.getParameter("state").equals(""))
//            throw new NullException("用户状态为空");
//        int state = Integer.valueOf(request.getParameter("state"));
        int state = 1;
        String wechat = request.getParameter("wechat");
        user.setState(state);
        user.setDepartment(department);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setWechat(wechat);
        String icon = request.getParameter("icon");
        user.setIcons(icon);
        user = userRepository.save(user);
        String roles = request.getParameter("roles");
        UserResponse userResponse = new UserResponse();
        if (!roles.equals("")) {
            // Save user's roles
            editRoles(user.getId(), roles);
            String userRoles = userRoleService.getUserRoleNamesByUserId(user.getId());
            userResponse.setUserResponse(user, userRoles);
        } else {
            // Save user's roles
            userResponse.setUserResponse(user, "");
        }

        return userResponse;
    }

    public User save(User user) { return userRepository.save(user); }

    public UserResponse findUserById(long id) {
        UserResponse res = new UserResponse();
        String roles = userRoleService.getUserRoleNamesByUserId(id);
        User user = userRepository.findById(id);
        res.setUserResponse(user, roles);
        return res;
    }

    public User findUserByPhone(String phone) {
        return userRepository.findUserByPhone(phone);
    }

    public void deleteUserById(long id) {
        // FIXME: We need to delete all user's resource in the future!
        int res = deleteUserResource(id);
        if (res == 0)
            userRepository.delete(id);
    }

    @Transactional
    public void deleteUserByPhone(String phone) {
        // FIXME: We need to delete all user's resource in the future!
        User user = userRepository.findUserByPhone(phone);
        if (user.getId() == 1)
            return;
        int res = deleteUserResource(user.getId());
        if (res == 0)
            deleteUserById(user.getId());
    }

    private int deleteUserResource(long id) {
        // TODO: Add this in the future!
        int res = 0;
        return res;
    }

    public User editPrivilege(long userId, String privileges, String add) throws NullException {
        User user = userRepository.findById(userId);
        if (user == null)
            throw new NullException("id错误，用户不存在");
        if (privileges.startsWith(","))
            privileges = privileges.substring(1, privileges.length());
        if (privileges.endsWith(","))
            privileges = privileges.substring(0, privileges.length() - 1);
        String []privilegesArr = privileges.split(",");
        if (privilegesArr.length <= 0)
            throw new NullException("权限值设置有误，无法解析");
        if (add.equals(""))
            throw new NullException("没有指定添加或是删除！");
        if (add.equals("1")) {
            List<UserPrivilege> userPrivileges = new ArrayList<>();
            List<PrivilegeConfig> privilegeConfigs = privilegeConfigService.findAll();
            Map<Long, PrivilegeConfig> menusMap = new HashMap<Long, PrivilegeConfig>();
            Map<Long, PrivilegeConfig> directoryMap = new HashMap<>();
            Map<Long, PrivilegeConfig> privilegeConfigMap = new HashMap<Long, PrivilegeConfig>();
            List<UserPrivilege> userPrivilegeList = userPrivilegeService.findByUserId(userId);
            List<Long> userPrivilegeIds = new ArrayList<>();
            for (PrivilegeConfig p: privilegeConfigs) {
                privilegeConfigMap.put(p.getId(), p);
                if (p.getType().equals("菜单"))
                    menusMap.put(p.getId(), p);
                if (p.getType().equals("目录"))
                    directoryMap.put(p.getId(), p);
            }
            Map<Long, UserPrivilege> userPrivilegeMap = new HashMap<>();
            for (UserPrivilege utemp : userPrivilegeList) {
                userPrivilegeMap.put(utemp.getPrivilege_id(), utemp);
                userPrivilegeIds.add(utemp.getPrivilege_id());
            }
            for (String id : privilegesArr) {
                if (id.equals(""))
                    continue;
                if (userPrivilegeMap.get(Long.valueOf(id)) != null)
                    continue;
                if (privilegeConfigMap.get(Long.valueOf(id)) == null)
                    continue;
                if (userPrivilegeIds.contains(Long.valueOf(id)))
                    continue;
                UserPrivilege userPrivilege = new UserPrivilege();
                userPrivilege.setPrivilege_id(Long.valueOf(id));
                userPrivilege.setUser_id(userId);
                PrivilegeConfig privilegeConfig = privilegeConfigMap.get(Long.valueOf(id));
                // Add the button who's name is the same as the menu's name, and add the menu 2
                PrivilegeConfig menu = menusMap.get(privilegeConfig.getPid());
                if (menu == null)
                    continue;
                if (!userPrivilegeIds.contains(menu.getId())) {
                    // Add menu to user's privilege
                    if (menu.getId() != Long.valueOf(id) && menu.getName().equals(privilegeConfig.getName())) {
                        UserPrivilege mUserPrivilege = new UserPrivilege();
                        mUserPrivilege.setPrivilege_id(menu.getId());
                        mUserPrivilege.setUser_id(userId);
                        mUserPrivilege.setPrivilege_name(menu.getName());
                        userPrivileges.add(mUserPrivilege);
                    }
                    PrivilegeConfig dir = directoryMap.get(menu.getPid());
                    if (!userPrivilegeIds.contains(dir.getId()) && menu.getName().equals(privilegeConfig.getName())) {
                        // Add directory to user's privilege
                        UserPrivilege dUserPrivilege = new UserPrivilege();
                        dUserPrivilege.setPrivilege_id(dir.getId());
                        dUserPrivilege.setUser_id(userId);
                        dUserPrivilege.setPrivilege_name(dir.getName());
                        userPrivileges.add(dUserPrivilege);
                    }
                }
                userPrivilege.setPrivilege_name(privilegeConfig.getName());
                userPrivileges.add(userPrivilege);
            }
            userPrivilegeService.save(userPrivileges);
        } else if (add.equals("0")) {
            for (String id : privilegesArr) {
                if (id.equals(""))
                    continue;
                // If the privilege'name is equal to its menu's name, delete all privileges in this menu
                // also delete the menu
                List<PrivilegeConfig> privilegeConfigs = privilegeConfigService.findAll();
                Map<Long, PrivilegeConfig> menusMap = new HashMap<Long, PrivilegeConfig>();
                Map<Long, PrivilegeConfig> directoryMap = new HashMap<>();
                Map<Long, PrivilegeConfig> privilegeConfigMap = new HashMap<Long, PrivilegeConfig>();
                List<UserPrivilege> userPrivilegeList = userPrivilegeService.findByUserId(userId);
                List<Long> userPrivilegeIds = new ArrayList<>();
                for (PrivilegeConfig p: privilegeConfigs) {
                    privilegeConfigMap.put(p.getId(), p);
                    if (p.getType().equals("菜单"))
                        menusMap.put(p.getId(), p);
                    if (p.getType().equals("目录"))
                        directoryMap.put(p.getId(), p);
                }
                Map<Long, UserPrivilege> userPrivilegeMap = new HashMap<>();
                for (UserPrivilege utemp : userPrivilegeList) {
                    userPrivilegeMap.put(utemp.getPrivilege_id(), utemp);
                    userPrivilegeIds.add(utemp.getPrivilege_id());
                }
                if (!userPrivilegeIds.contains(Long.valueOf(id)))
                    continue;
                UserPrivilege userPrivilege = userPrivilegeMap.get(Long.valueOf(id));
                PrivilegeConfig privilegeConfig = privilegeConfigMap.get(userPrivilege.getPrivilege_id());
                PrivilegeConfig mPrivilege = menusMap.get(privilegeConfig.getPid());
                if (mPrivilege == null) {
                    userPrivilegeService.deletePrivilegeByUserId(userId, Long.valueOf(id));
                    continue;
                }

                PrivilegeConfig dPrivilege = directoryMap.get(mPrivilege.getPid());
                List<Long> ids = new ArrayList<>();
                ids.add(Long.valueOf(id));
                if (mPrivilege.getName().equals(userPrivilege.getPrivilege_name())) {
                    // menu button
                    ids.add(mPrivilege.getId());
                    // FIXME: Delete all buttons in this menu
//                    List<PrivilegeConfig> menusPivileges = privilegeConfigService.getByIdsAndPid(userPrivilegeIds, mPrivilege.getId());
//                    for (PrivilegeConfig p : menusPivileges) {
//                        ids.add(p.getId());
//                    }
                    // FIXME: If the directory has no menu, delete it
                    if (dPrivilege == null) {
                        userPrivilegeService.deletePrivilegesByUserId(userId, ids);
                        continue;
                    }
                    List<PrivilegeConfig> menusInDir = privilegeConfigService.findByPid(dPrivilege.getId());
                    boolean deleteDir = true;
                    for (PrivilegeConfig p : menusInDir) {
                        if (p.getId() == mPrivilege.getId())
                            continue;
                        if (userPrivilegeIds.contains(p.getId())) {
                            deleteDir = false;
                            break;
                        }
                    }
                    if (deleteDir)
                        ids.add(dPrivilege.getId());
                    userPrivilegeService.deletePrivilegesByUserId(userId, ids);

                } else {
                    userPrivilegeService.deletePrivilegeByUserId(userId, Long.valueOf(id));
                }
            }
        }
        return user;
    }

    public User editRoles(long userId, String rolesStr) throws NullException {
        User user = userRepository.findById(userId);
        if (user == null)
            throw new NullException("id错误，用户不存在");
        if (rolesStr.startsWith(","))
            rolesStr = rolesStr.substring(1, rolesStr.length());
        if (rolesStr.endsWith(","))
            rolesStr = rolesStr.substring(0, rolesStr.length() - 1);
        String []rolesArr = rolesStr.split(",");
        if (rolesArr.length <= 0)
            throw new NullException("角色值设置有误，无法解析");
        // Delete all first
        // Also need to delete all privileges belonged to this role
        List<UserRole> userRolesNow = userRoleService.findByUserId(userId);
        if (!userRolesNow.isEmpty()) {
            List<Long> rolePrivilegeIds = new ArrayList<>();
            List<Long> roleIdsNow = new ArrayList<>();
            for (UserRole u : userRolesNow) {
                roleIdsNow.add(u.getRole_id());
            }
            List<RolePrivilege> rolePrivilegeList = rolePrivilegeService.findByRoleIds(roleIdsNow);
            for (RolePrivilege r : rolePrivilegeList) {
                rolePrivilegeIds.add(r.getPrivilege_id());
            }
            if (!rolePrivilegeIds.isEmpty())
                userPrivilegeService.deletePrivilegesByUserId(userId, rolePrivilegeIds);
        }
        userRoleService.deleteByUserId(userId);
        // Then, add
        List<UserRole> userRoles = new ArrayList<>();

        List<Role> roles = roleService.findAllNoPage();
        Map<String, Role> rolesMap = new HashMap<>();
        for (Role r: roles) {
            rolesMap.put(r.getName(), r);
        }
        for (String name : rolesArr) {
            if (name.equals(""))
                continue;
            if (!rolesMap.containsKey(name))
                continue;
            UserRole userRole = new UserRole();
            userRole.setUser_id(userId);
            userRole.setRole_id(rolesMap.get(name).getId());
            userRole.setRole_name(name);
            //userRoleService.save(userRole);
            userRoles.add(userRole);
        }
        userRoleService.save(userRoles);
        setUserPrivilegeByRoles(userRoles, userId);
        return user;
    }

    public UserResponse getUserInfo(User user) throws Exception {
        List<UserPrivilege> userPrivileges = userPrivilegeService.findByUserId(user.getId());
        List<Long> privilegeIds = new ArrayList<>();
        UserResponse userResponse = new UserResponse();
        String userRoles = userRoleService.getUserRoleNamesByUserId(user.getId());
        userResponse.setUserResponse(user, userRoles);
        List<PrivilegeConfig> res = new ArrayList<>();

        if (user.getId() == 1) {
            // super admin
            res = privilegeConfigService.findAllTree();
        } else {
            for (UserPrivilege userPrivilege : userPrivileges) {
                privilegeIds.add(userPrivilege.getPrivilege_id());
            }
            if (privilegeIds.isEmpty()) {
                userResponse.setPrivileges(res);
                return userResponse;
            }
            int level = 1;
            List<PrivilegeConfig> temp = privilegeConfigService.getByIdsAndLevel(privilegeIds, level);
            if (temp.size() > 0) {
                for (PrivilegeConfig p1 : temp) {
                    if (StringUtils.isNotBlank(p1.getValue()))
                        res.add(p1);
                    List<PrivilegeConfig> level2 = privilegeConfigService.getByIdsAndPid(privilegeIds, p1.getId());
                    if (level2.size() > 0) {
                        for (PrivilegeConfig p2 : level2) {
                            if (StringUtils.isNotBlank(p2.getValue()))
                                res.add(p2);
                            List<PrivilegeConfig> level3 = privilegeConfigService.getByIdsAndPid(privilegeIds, p2.getId());
                            if (level3.size() > 0) {
                                for (PrivilegeConfig p3 : level3) {
                                    if (StringUtils.isNotBlank(p3.getValue()))
                                        res.add(p3);
                                }
                            }
                        }
                    }
                }
            } else {
                level = 2;
                temp = privilegeConfigService.getByIdsAndLevel(privilegeIds, level);
                if (temp.size() > 0) {
                    for (PrivilegeConfig p2 : temp) {
                        if (StringUtils.isNotBlank(p2.getValue()))
                            res.add(p2);
                        List<PrivilegeConfig> level3 = privilegeConfigService.getByIdsAndPid(privilegeIds, p2.getId());
                        if (level3.size() > 0) {
                            for (PrivilegeConfig p3 : level3) {
                                if (StringUtils.isNotBlank(p3.getValue()))
                                    res.add(p3);
                            }
                        }
                    }

                } else {
                    level = 3;
                    temp = privilegeConfigService.getByIdsAndLevel(privilegeIds, level);
                    for (PrivilegeConfig p : temp) {
                        if (StringUtils.isNotBlank(p.getValue()))
                            res.add(p);
                    }
                }
            }

        }


        userResponse.setPrivileges(res);
        return userResponse;
    }

    private void setUserPrivilegeByRoles(List<UserRole> roles, long userId) {
        List<UserPrivilege> userPrivileges = new ArrayList<>();
        List<UserPrivilege> userPrivilegesNow = userPrivilegeService.findByUserId(userId);
        List<Long> userPrivilegesIds = new ArrayList<>();
        for (UserPrivilege u : userPrivilegesNow) {
            userPrivilegesIds.add(u.getId());
        }
        for (UserRole userRole : roles) {
            List<RolePrivilege> rolePrivileges = rolePrivilegeService.findByRoleId(userRole.getRole_id());
            for (RolePrivilege rolePrivilege : rolePrivileges) {
                if (userPrivilegesIds.contains(rolePrivilege.getPrivilege_id()))
                    continue;
                UserPrivilege userPrivilege = new UserPrivilege();
                userPrivilege.setUser_id(userRole.getUser_id());
                userPrivilege.setPrivilege_id(rolePrivilege.getPrivilege_id());
                userPrivilege.setPrivilege_name(rolePrivilege.getPrivilege_name());
                userPrivileges.add(userPrivilege);
                userPrivilegesIds.add(rolePrivilege.getPrivilege_id());
            }
        }
        userPrivilegeService.save(userPrivileges);
    }


    public Iterable<UserPrivilege> getUserPrivileges(long userId) throws NullException {
        User user = userRepository.findById(userId);
        if (user == null)
            throw new NullException("id错误，用户不存在");
        List<UserPrivilege> userPrivileges = userPrivilegeService.findByUserId(userId);
        return userPrivileges;
    }


    public Iterable<UserRole> getUserRoles(long userId) throws NullException {
        User user = userRepository.findById(userId);
        if (user == null)
            throw new NullException("id错误，用户不存在");
        Iterable<UserRole> userRoles = userRoleService.findByUserId(userId);
        return userRoles;
    }

    public void setPassword(String oldPassword, String newPassword, User user) throws Exception {
        //String op = EncryptionAlgs.getMD5(oldPassword);
        if (StringUtils.isBlank(newPassword))
            throw new Exception("新密码不能为空");
        String op = oldPassword;
        if (!op.equals(user.getPassword()))
            throw new Exception("旧密码正取");
        //String np = EncryptionAlgs.getMD5(newPassword);
        String np = newPassword;
        user.setPassword(np);
        if (user.getHas_login() == 0)
            user.setHas_login(1);
        userRepository.save(user);
    }

    public void resetPassword(long userId) throws Exception {
        User user = userRepository.findById(userId);
        if (user == null)
            throw new NullException("无此id用户");
        String password = "111111";
        String np = EncryptionAlgs.getMD5(password);
        user.setPassword(password);
        userRepository.save(user);
    }

    public User bindWechat(HttpServletRequest request, User user) throws Exception {
        String code = request.getParameter("code");
        SnsToken snsToken = SnsAPI.oauth2AccessToken(Global.wechatAppId, Global.wechatSecretKey, code);
        UserWechat userWechat = null;
        if (snsToken.isSuccess())
            userWechat = SnsAPI.userinfo(snsToken.getAccess_token(), snsToken.getOpenid(), "zh_CN");
        userWechatService.save(userWechat);
        user.bindWechat(userWechat);
        user = userRepository.save(user);
        user.setPassword("");
        return user;
    }

    public User join(String name,
                     String phone,
                     String verifyCode,
                     String password,
                     HttpServletRequest request) throws Exception {
        // Need to check verifyCode first
        if (!checkVerifyCode(null, phone, verifyCode, request.getRemoteHost()))
            throw new Exception("验证码校验错误");
        User user = null;
        user = userRepository.findUserByPhone(phone);
        if (user != null)
            throw new Exception("用户已存在");
        user = new User();
        user.setPassword(password);
        user.setName(name);
        user.setPhone(phone);
        user.setLast_ip(request.getRemoteHost());
        user = userRepository.save(user);
        user.setPassword("");
        return user;
    }

    public User wechatJoin(String name,
                           String phone,
                           String verifyCode,
                           String password,
                           User user) throws Exception {
        if (!checkVerifyCode(user.getId(), phone, verifyCode, user.getLast_ip()))
            throw new Exception("验证码校验错误");
        User user1 = userRepository.findUserByPhone(phone);
        if (user1 != null)
            throw new Exception("用户已存在");
        user.setPassword(password);
        user.setName(name);
        user.setHas_join(1);
        return user;
    }

    private boolean checkVerifyCode(Long userId, String phoneNum, String code, String ip) throws Exception {
        return true;
    }
}
