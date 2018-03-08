package com.luotuo.user.repository;

import com.luotuo.user.entity.UserResources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserResourcesRepository extends JpaRepository<UserResources, Long>,JpaSpecificationExecutor<UserResources> {
    @Modifying
    @Query(value = "delete from UserResources bean where bean.resourceId = ?1")
    void deleteByResourceId(String resourceId);

    @Modifying
    @Query(value = "delete from UserResources bean where bean.userId in ?1")
    void deleteByUserIds(List<Long> userIds);

    @Modifying
    @Query(value = "delete from UserResources bean where bean.resourceId in ?1")
    void deleteByResourceIds(List<Long> resourceIds);
}
