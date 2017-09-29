package com.jwcq.user.repository;

import com.jwcq.user.entity.Department;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by luotuo on 17-7-3.
 */
@Repository
@Table(name = "department")
@Qualifier("privilegeConfigRepository")
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Department findById(long id);

    @Query(value="select bean from Department bean where level = ?1", nativeQuery = false)
    List findByLevel(int level);

    @Query(value="select bean from Department bean where pid = ?1", nativeQuery = false)
    List findByPid(long pid);

    @Query(value = "select bean from Department bean where name = ?1")
    List<Department> getByName(String name);
}
