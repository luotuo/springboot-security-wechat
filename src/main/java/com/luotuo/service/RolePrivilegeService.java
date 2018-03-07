package com.luotuo.service;

import com.luotuo.user.entity.RolePrivilege;
import com.luotuo.user.repository.RolePrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by luotuo on 17-7-13.
 */
@Service
@Transactional("secondTransactionManager")
public class RolePrivilegeService {

    @Autowired
    private RolePrivilegeRepository rolePrivilegeRepository;

    public void deleteByRoleId(long roleId) {

    }

    public void deleteByPrivilegeId(long privilegeId) {

    }

    public Iterable<RolePrivilege> save(Iterable<RolePrivilege> privileges) {
        return rolePrivilegeRepository.save(privileges);
    }

    public List<RolePrivilege> findByRoleId(long roleId) {
        return rolePrivilegeRepository.findByRole_id(roleId);
    }

    public List<RolePrivilege> findByRoleIds(List<Long> roleIds) {
        return rolePrivilegeRepository.findByRoleIds(roleIds);
    }

    @Transactional
    public void deletePrivilegeByRoleId(long roleId, long privilegeId) {
        rolePrivilegeRepository.deletePrivilegeByRoleId(roleId, privilegeId);
    }

    @Transactional
    public void deletePrivilegesByRoleId(long roleId, List<Long> privilegsIds) {
        rolePrivilegeRepository.deletePrivilegesByRoleId(roleId, privilegsIds);
    }



}
