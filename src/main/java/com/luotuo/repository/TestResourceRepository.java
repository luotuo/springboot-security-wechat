package com.luotuo.repository;

import com.luotuo.entity.TestResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TestResourceRepository extends JpaRepository<TestResource, Long> {
}
