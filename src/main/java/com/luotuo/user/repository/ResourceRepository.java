package com.luotuo.user.repository;

import com.luotuo.user.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>,JpaSpecificationExecutor<Resource> {

    @Query(value = "select bean from Resource bean where bean.resourceId = ?1")
    Resource findByResourceId(String resourceId);
}
