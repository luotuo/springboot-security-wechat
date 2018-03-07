package com.luotuo.service;

import com.luotuo.user.entity.PrivilegeConfig;
import com.luotuo.user.entity.UserPrivilege;
import com.luotuo.user.repository.UserPrivilegeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luotuo on 17-6-30.
 */
@Service
@Transactional("secondTransactionManager")
public class UserPrivilegeService {
    @Autowired
    private UserPrivilegeRepository userPrivilegeRepository;

    @Transactional
    public void deleteByUserId(long userId) {
        userPrivilegeRepository.deleteByUser_id(userId);
    }

    public Iterable<UserPrivilege> save(Iterable<UserPrivilege> privileges) {
        return userPrivilegeRepository.save(privileges);
    }

    public List<UserPrivilege> findByUserId(long userId) {
        return userPrivilegeRepository.findByUser_id(userId);
    }

    @Transactional
    public void deletePrivilegeByUserId(long userId, long privilegeId) {
        userPrivilegeRepository.deletePrivilegeByUserId(userId, privilegeId);
    }

    @Transactional
    public void deletePrivilegesByUserId(long userId, List<Long> privilegeIds) {
        userPrivilegeRepository.deletePrivilegesByUserId(userId, privilegeIds);
    }

    @Transactional
    public void deleteByPrivilegeId(long privilegeId) {
        userPrivilegeRepository.deleteByPrivilegeId(privilegeId);
    }

    @Transactional
    public void deleteByPrivilegeIds(List<Long> privilegeIds) {
        userPrivilegeRepository.deleteByPrivilegeIds(privilegeIds);
    }

    @Transactional
    public void deletePrivilegeByUserIdsAndPrivilegeId(List<Long> userIds, long privilegeId) {
        userPrivilegeRepository.deleteByUserIdsAndPrivilegeId(userIds, privilegeId);
    }

    @Transactional
    public void deletePrivilegeByUserIdsAndPrivilegeIds(List<Long> userIds, List<Long> privilegeIds) {
        userPrivilegeRepository.deleteByUserIdsAndPrivilegeIds(userIds, privilegeIds);
    }

    public void addPrivilegesForUsers(List<Long> userIds, List<PrivilegeConfig> privileges) {
        List<UserPrivilege> userPrivileges = new ArrayList<>();
        for (Long u : userIds) {
            for (PrivilegeConfig p : privileges) {
                UserPrivilege userPrivilege = new UserPrivilege();
                userPrivilege.setPrivilege_name(p.getName());
                userPrivilege.setUser_id(u);
                userPrivilege.setPrivilege_id(p.getId());
                userPrivileges.add(userPrivilege);
            }
        }
        if (!userPrivileges.isEmpty())
            userPrivilegeRepository.save(userPrivileges);
    }
}
