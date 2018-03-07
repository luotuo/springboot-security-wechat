package com.luotuo.user.repository;

import com.luotuo.user.entity.UserResources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserResourcesRepository extends JpaRepository<UserResources, Long>,JpaSpecificationExecutor<UserResources> {
}
