package com.luotuo.user.repository;

import com.luotuo.user.entity.UserRole;
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
@Table(name = "user_role")
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Modifying
    @Query(value="delete from UserRole bean where user_id = ?1")
    void deleteByUser_id(Long userId);

    @Query(value = "select bean from UserRole bean where user_id=?1")
    List<UserRole> findByUser_id(long userId);

    @Query(value = "select bean from UserRole bean where role_name=?1")
    List<UserRole> findByName(String name);

    @Query(value = "select bean from UserRole bean where role_id=?1")
    List<UserRole> findByRole_id(Long id);

    @Query(value = "select bean from UserRole bean where role_id in ?1")
    List<UserRole> findByRoleIds(List<Long> roleIds);
}
