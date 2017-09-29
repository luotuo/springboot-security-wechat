package com.jwcq.user.repository;

import com.jwcq.user.entity.RolePrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by luotuo on 17-7-13.
 */
@Repository
@Table(name = "role_privilege")
public interface RolePrivilegeRepository extends JpaRepository<RolePrivilege, Long> {
    @Query(value = "select bean from RolePrivilege bean where role_id=?1")
    List<RolePrivilege> findByRole_id(Long roleId);

    @Modifying
    @Query(value = "delete from RolePrivilege bean where role_id = ?1 and privilege_id = ?2")
    void deletePrivilegeByRoleId(Long roleId, Long privilegeId);

    @Modifying
    @Query(value = "delete from RolePrivilege bean where role_id = ?1 and privilege_id in ?2")
    void deletePrivilegesByRoleId(Long roleId, List<Long> privilegeIds);

    @Query(value = "select bean from RolePrivilege bean where role_id in ?1")
    List<RolePrivilege> findByRoleIds(List<Long> roleIds);
}
