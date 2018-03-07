package com.luotuo.user.repository;

import com.luotuo.user.entity.UserPrivilege;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by luotuo on 17-6-30.
 */
@Repository
@Table(name = "user_privilege")
public interface UserPrivilegeRepository extends JpaRepository<UserPrivilege, Long> {
    @Modifying
    @Query(value="delete from UserPrivilege bean where user_id = ?1", nativeQuery = false)
    void deleteByUser_id(Long userId);

    @Query(value = "select bean from UserPrivilege bean where user_id=?1")
    List<UserPrivilege> findByUser_id(Long userId);

    @Modifying
    @Query(value="delete from UserPrivilege bean where user_id = ?1 and privilege_id = ?2", nativeQuery = false)
    void deletePrivilegeByUserId(Long userId, long privilegeId);

    @Modifying
    @Query(value = "delete from UserPrivilege bean where user_id = ?1 and privilege_id in ?2")
    void deletePrivilegesByUserId(Long userId, List<Long> privilegeIds);

    @Modifying
    @Query(value = "delete from UserPrivilege bean where privilege_id = ?1")
    void deleteByPrivilegeId(Long privilegeId);

    @Modifying
    @Query(value = "delete from UserPrivilege bean where privilege_id in ?1")
    void deleteByPrivilegeIds(List<Long> privilegeIds);

    @Modifying
    @Query(value = "delete from UserPrivilege bean where user_id in ?1 and privilege_id = ?2")
    void deleteByUserIdsAndPrivilegeId(List<Long> userIds, Long privilegeId);

    @Modifying
    @Query(value = "delete from UserPrivilege bean where user_id in ?1 and privilege_id in ?2")
    void deleteByUserIdsAndPrivilegeIds(List<Long> userIds, List<Long> privilegeIds);
}
