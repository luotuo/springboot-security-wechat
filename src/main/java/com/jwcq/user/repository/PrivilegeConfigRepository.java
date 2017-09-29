package com.jwcq.user.repository;

import com.jwcq.user.entity.PrivilegeConfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by luotuo on 17-6-30.
 */
@Repository
@Table(name = "privilege_config")
@Qualifier("privilegeConfigRepository")
public interface PrivilegeConfigRepository extends JpaRepository<PrivilegeConfig, Long> {
    PrivilegeConfig findById(long id);

    @Query(value="select bean from PrivilegeConfig bean where level = ?1", nativeQuery = false)
    List findByLevel(int level);
    @Query(value="select bean from PrivilegeConfig bean where pid = ?1", nativeQuery = false)
    List findByPid(long pid);

    @Query(value = "select id, platform, `name` from privilege_config where `type`='菜单' GROUP BY id, platform, `name`", nativeQuery = true)
    List<Object[]> findPlatformsAndMenus();

    @Query(value = "select bean from PrivilegeConfig bean where id in ?1")
    List<PrivilegeConfig> findByIds(List<Long> ids);

    @Query(value = "select bean from PrivilegeConfig bean where id in ?1 and level=?2")
    List<PrivilegeConfig> findByIdsAndLevel(List<Long> ids, Integer level);

    @Query(value = "select bean from PrivilegeConfig bean where id in ?1 and pid = ?2")
    List<PrivilegeConfig> findByIdsAndPid(List<Long> ids, Long pid);
}
