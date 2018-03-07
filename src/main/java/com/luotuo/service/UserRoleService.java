package com.luotuo.service;

import com.luotuo.user.entity.UserRole;
import com.luotuo.user.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by luotuo on 17-7-13.
 */
@Service
@Transactional("secondTransactionManager")
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Transactional
    public void deleteByUserId(long userId) {
        userRoleRepository.deleteByUser_id(userId);
    }

    public List<UserRole> save(Iterable<UserRole> userRoles) {
        return userRoleRepository.save(userRoles);
    }

    public UserRole save(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    public List<UserRole> findByUserId(long userId) {
        return userRoleRepository.findByUser_id(userId);
    }

    public List<UserRole> findByName(String name) { return userRoleRepository.findByName(name); }

    public List<UserRole> findByRoleId(long id) { return userRoleRepository.findByRole_id(id); }

    public String getUserRoleNamesByUserId(long userId) {
        String res = "";
        List<UserRole> userRoles = findByUserId(userId);
        if (userRoles.size() <= 0)
            return res;
        for (UserRole u : userRoles) {
            res += u.getRole_name() + ",";
        }
        res = res.substring(0, res.length() - 1);
        return res;
    }

    public List<UserRole> findByRoleIds(List<Long> roleIds) {
        return userRoleRepository.findByRoleIds(roleIds);
    }
}
