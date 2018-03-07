package com.luotuo.user.repository;

import com.luotuo.user.entity.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;

/**
 * Created by luotuo on 17-7-4.
 */
@Repository
@Table(name = "roles")
@Qualifier("rolesRepository")
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByCode(String code);
    Role findById(long id);
    @Modifying
    @Query(value="delete from Role bean where code = :code", nativeQuery = false)
    void deleteByCode(@Param("code") String code);
}
