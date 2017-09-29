package com.jwcq.service;

import com.jwcq.MyExceptions.NullException;
import com.jwcq.user.entity.*;
import com.jwcq.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luotuo on 17-7-4.
 */
@Service
@Transactional("secondTransactionManager")
public class RoleService {
    @Autowired
    private RoleRepository rolesRepository;

    @Autowired
    private PrivilegeConfigService privilegeConfigService;

    @Autowired
    private RolePrivilegeService rolePrivilegeService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserPrivilegeService userPrivilegeService;

    public Iterable<Role> findAll(int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(page, pageSize, sort);
        Iterable<Role> roles = rolesRepository.findAll(pageRequest);
        return roles;
    }

    public List<Role> findAllNoPage() {
        return rolesRepository.findAll();
    }

    public Role save(Role r) {
        return rolesRepository.save(r);
    }

    public Role findByCode(String code) {
        return rolesRepository.findByCode(code);
    }

    public Role findById(Long id) {
        return rolesRepository.findById(id);
    }

    public void deleteById(Long id) {
        rolesRepository.delete(id);
    }

    @Transactional
    public void deleteByCode(String code) {
        rolesRepository.deleteByCode(code);
    }

//    public Role editPrivilege(long roleId, String privileges) throws NullException {
//        Role role = rolesRepository.findById(roleId);
//        if (role == null)
//            throw new NullException("id错误，角色不存在");
//        if (privileges.startsWith(","))
//            privileges = privileges.substring(1, privileges.length());
//        if (privileges.endsWith(","))
//            privileges = privileges.substring(0, privileges.length() - 1);
//        String []privilegesArr = privileges.split(",");
//        if (privilegesArr.length <= 0)
//            throw new NullException("权限值设置有误，无法解析");
//        // Delete all first
//        rolePrivilegeService.deleteByRoleId(roleId);
//        // Then, add
//        List<RolePrivilege> rolePrivileges = new ArrayList<>();
//
//        List<PrivilegeConfig> privilegeConfigs = privilegeConfigService.findAll();
//        Map<Long, PrivilegeConfig> privilegeConfigMap = new HashMap<Long, PrivilegeConfig>();
//        for (PrivilegeConfig p: privilegeConfigs) {
//            privilegeConfigMap.put(p.getId(), p);
//        }
//        for (String id : privilegesArr) {
//            RolePrivilege rolePrivilege = new RolePrivilege();
//            rolePrivilege.setPrivilege_id(Long.valueOf(id));
//            rolePrivilege.setRole_id(role.getId());
//            rolePrivilege.setRole_name(role.getName());
//            PrivilegeConfig privilegeConfig = privilegeConfigMap.get(Long.valueOf(id));
//            rolePrivilege.setPrivilege_name(privilegeConfig.getName());
//            rolePrivileges.add(rolePrivilege);
//        }
//        rolePrivilegeService.save(rolePrivileges);
//        return role;
//    }

    public Role editPrivilege(long roleId, String privileges, String add) throws Exception {
        Role role = rolesRepository.findById(roleId);
        if (role == null)
            throw new NullException("id错误，角色不存在");
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
            List<RolePrivilege> rolePrivileges = new ArrayList<>();
            List<PrivilegeConfig> privilegeConfigs = privilegeConfigService.findAll();
            Map<Long, PrivilegeConfig> privilegeConfigMap = new HashMap<Long, PrivilegeConfig>();
            List<RolePrivilege> rolePrivilegeList = rolePrivilegeService.findByRoleId(roleId);
            List<Long> rolePrivilegeIds = new ArrayList<>();
            Map<Long, PrivilegeConfig> mPrivilegesMap = new HashMap<>();
            Map<Long, PrivilegeConfig> dPrivilegesMap = new HashMap<>();
            for (PrivilegeConfig p: privilegeConfigs) {
                privilegeConfigMap.put(p.getId(), p);
                if (p.getType().equals("菜单")) mPrivilegesMap.put(p.getId(), p);
                if (p.getType().equals("目录")) dPrivilegesMap.put(p.getId(), p);
            }
            Map<Long, RolePrivilege> rolePrivilegeMap = new HashMap<>();
            for (RolePrivilege utemp : rolePrivilegeList) {
                rolePrivilegeMap.put(utemp.getPrivilege_id(), utemp);
                rolePrivilegeIds.add(utemp.getPrivilege_id());
            }
            for (String id : privilegesArr) {
                if (id.equals(""))
                    continue;
                if (rolePrivilegeMap.get(Long.valueOf(id)) != null)
                    continue;
                if (privilegeConfigMap.get(Long.valueOf(id)) == null)
                    continue;
                if (rolePrivilegeIds.contains(Long.valueOf(id)))
                    continue;
                PrivilegeConfig pNow = privilegeConfigMap.get(Long.valueOf(id));
                PrivilegeConfig mPNow = mPrivilegesMap.get(pNow.getPid());
                if (mPNow == null)
                    continue;
                if (!rolePrivilegeIds.contains(mPNow.getId())) {
                    // Add menu
                    if (mPNow.getId() != Long.valueOf(id) && mPNow.getName().equals(pNow.getName())) {
                        RolePrivilege mRolePrivilege = new RolePrivilege();
                        mRolePrivilege.setPrivilege_id(mPNow.getId());
                        mRolePrivilege.setRole_id(roleId);
                        mRolePrivilege.setPrivilege_name(mPNow.getName());
                        mRolePrivilege.setRole_name(role.getName());
                        rolePrivileges.add(mRolePrivilege);
                    }
                    PrivilegeConfig dPNow = dPrivilegesMap.get(mPNow.getPid());
                    if (!rolePrivilegeIds.contains(dPNow.getId()) && mPNow.getName().equals(pNow.getName())) {
                        // Add directory to role's privilege
                        RolePrivilege dRolePrivilege = new RolePrivilege();
                        dRolePrivilege.setPrivilege_id(dPNow.getId());
                        dRolePrivilege.setRole_id(roleId);
                        dRolePrivilege.setPrivilege_name(dPNow.getName());
                        dRolePrivilege.setRole_name(role.getName());
                        rolePrivileges.add(dRolePrivilege);
                    }
                }
                RolePrivilege rolePrivilege = new RolePrivilege();
                rolePrivilege.setPrivilege_id(Long.valueOf(id));
                rolePrivilege.setRole_id(roleId);
                rolePrivilege.setPrivilege_name(pNow.getName());
                rolePrivilege.setRole_name(role.getName());
                rolePrivileges.add(rolePrivilege);
            }
            rolePrivilegeService.save(rolePrivileges);
            addUserPrivilegesByRole(rolePrivileges);
        } else {
            for (String id : privilegesArr) {
                if (id.equals(""))
                    continue;
                // If the privilege'name is equal to its menu's name, delete all privileges in this menu
                // also delete the menu
                List<RolePrivilege> rolePrivileges = new ArrayList<>();
                List<PrivilegeConfig> privilegeConfigs = privilegeConfigService.findAll();
                Map<Long, PrivilegeConfig> privilegeConfigMap = new HashMap<Long, PrivilegeConfig>();
                List<RolePrivilege> rolePrivilegeList = rolePrivilegeService.findByRoleId(roleId);
                List<Long> rolePrivilegeIds = new ArrayList<>();
                Map<Long, PrivilegeConfig> mPrivilegesMap = new HashMap<>();
                Map<Long, PrivilegeConfig> dPrivilegesMap = new HashMap<>();
                for (PrivilegeConfig p: privilegeConfigs) {
                    privilegeConfigMap.put(p.getId(), p);
                    if (p.getType().equals("菜单")) mPrivilegesMap.put(p.getId(), p);
                    if (p.getType().equals("目录")) dPrivilegesMap.put(p.getId(), p);
                }
                Map<Long, RolePrivilege> rolePrivilegeMap = new HashMap<>();
                for (RolePrivilege utemp : rolePrivilegeList) {
                    rolePrivilegeMap.put(utemp.getPrivilege_id(), utemp);
                    rolePrivilegeIds.add(utemp.getPrivilege_id());
                }
                if (!rolePrivilegeIds.contains(Long.valueOf(id)))
                    continue;


                RolePrivilege rolePrivilege = rolePrivilegeMap.get(Long.valueOf(id));
                PrivilegeConfig privilegeConfig = privilegeConfigMap.get(rolePrivilege.getPrivilege_id());
                PrivilegeConfig mPrivilege = mPrivilegesMap.get(privilegeConfig.getPid());
                if (mPrivilege == null) {
                    rolePrivilegeService.deletePrivilegeByRoleId(roleId, Long.valueOf(id));
                    deleteUserPrivilegeByRole(roleId, Long.valueOf(id));
                    continue;
                }

                PrivilegeConfig dPrivilege = dPrivilegesMap.get(mPrivilege.getPid());
                List<Long> ids = new ArrayList<>();
                ids.add(Long.valueOf(id));
                if (mPrivilege.getName().equals(rolePrivilege.getPrivilege_name())) {
                    // menu button
                    ids.add(mPrivilege.getId());
                    // FIXME: Delete all buttons in this menu
//                    List<PrivilegeConfig> menusPivileges = privilegeConfigService.getByIdsAndPid(rolePrivilegeIds, mPrivilege.getId());
//                    for (PrivilegeConfig p : menusPivileges) {
//                        ids.add(p.getId());
//                    }
                    // FIXME: If the directory has no menu, delete it
                    if (dPrivilege == null) {
                        rolePrivilegeService.deletePrivilegesByRoleId(roleId, ids);
                        deleteUserPrivilegesByRole(roleId, ids);
                        continue;
                    }
                    List<PrivilegeConfig> menusInDir = privilegeConfigService.findByPid(dPrivilege.getId());
                    boolean deleteDir = true;
                    for (PrivilegeConfig p : menusInDir) {
                        if (p.getId() == mPrivilege.getId())
                            continue;
                        if (rolePrivilegeIds.contains(p.getId())) {
                            deleteDir = false;
                            break;
                        }
                    }
                    if (deleteDir)
                        ids.add(dPrivilege.getId());
                    rolePrivilegeService.deletePrivilegesByRoleId(roleId, ids);
                    deleteUserPrivilegesByRole(roleId, ids);

                } else {
                    rolePrivilegeService.deletePrivilegeByRoleId(roleId, Long.valueOf(id));
                    deleteUserPrivilegeByRole(roleId, Long.valueOf(id));
                }
            }
        }
        return role;
    }

    public Iterable<RolePrivilege> getRolePrivileges(long roleId) throws NullException {
        Role role = rolesRepository.findById(roleId);
        if (role == null)
            throw new NullException("id错误，角色不存在");
        List<RolePrivilege> rolePrivileges = rolePrivilegeService.findByRoleId(roleId);
        return rolePrivileges;
    }

    private void deleteUserPrivilegeByRole(long roleId, long privilegeId) throws Exception {
        List<Long> privilegeIds = new ArrayList<>();
        privilegeIds.add(privilegeId);
        List<UserRole> userRoles = userRoleService.findByRoleId(roleId);
        if (userRoles.isEmpty())
            return;
        List<Long> userIds = new ArrayList<>();
        for (UserRole u : userRoles) {
            userIds.add(u.getUser_id());
        }
        deleteUserPrivilegesByRole(userIds, privilegeIds);
    }

    private void deleteUserPrivilegesByRole(long roleId, List<Long> privilegeIds) throws Exception {
        List<UserRole> userRoles = userRoleService.findByRoleId(roleId);
        if (userRoles.isEmpty())
            return;
        List<Long> userIds = new ArrayList<>();
        for (UserRole u : userRoles) {
            userIds.add(u.getUser_id());
        }
        deleteUserPrivilegesByRole(userIds, privilegeIds);
    }

    private void deleteUserPrivilegesByRole(List<Long> userIds, List<Long> privilegeIds) throws Exception {
        userPrivilegeService.deletePrivilegeByUserIdsAndPrivilegeIds(userIds, privilegeIds);
    }

    private void addUserPrivilegesByRole(List<RolePrivilege> rolePrivileges) throws Exception {
        List<Long> privilegeIds = new ArrayList<>();
        List<Long> roleIds = new ArrayList<>();
        for (RolePrivilege r : rolePrivileges) {
            privilegeIds.add(r.getPrivilege_id());
            roleIds.add(r.getRole_id());
        }
        List<UserRole> userRoles = userRoleService.findByRoleIds(roleIds);
        if (userRoles.isEmpty())
            return;
        List<Long> userIds = new ArrayList<>();
        for (UserRole u : userRoles) {
            userIds.add(u.getUser_id());
        }
        addUserPrivilegesByRole(userIds, privilegeIds);
    }

    private void addUserPrivilegesByRole(List<Long> userIds, List<Long> privilegeIds) throws Exception {
        List<PrivilegeConfig> privilegeConfigs = privilegeConfigService.getByIds(privilegeIds);
        userPrivilegeService.addPrivilegesForUsers(userIds, privilegeConfigs);
    }

}
