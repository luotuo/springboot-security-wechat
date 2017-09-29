package com.jwcq.user.repository;

import com.jwcq.user.entity.User;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by luotuo on 17-6-30.
 */
@Repository
@Table(name = "user")
@Qualifier("userRepository")
public interface UserRepository extends JpaRepository<User, Long>,JpaSpecificationExecutor<User> {
    User findById(Long id);
    @Query(value="select bean from User bean where email=?1")
    User getUserByLoginName(String name);
    @Query(value="select bean from User bean where phone=?1")
    User findUserByPhone(String code);

    @Modifying
    @Query(value="delete from User bean where code = ?1")
    void deleteByCode(String code);

    List<User> findByName(String username);


    @Query(value = "select bean from User bean where state=?4 and name like %?1% and phone like %?2% and department like %?3%")
    Page search(String name, String phone, String department, Integer state, Pageable pageable);

    @Query(value = "select bean from User bean where name like %?1% and phone like %?2% and department like %?3%")
    Page searchNoState(String name, String phone, String department, Pageable pageable);

    @Query(value = "select bean from User bean where state=?4 and name like %?1% and phone like %?2% and department like %?3% and id in ?5")
    Page searchWithRole(String name, String phone, String department, Integer state, List<Long> ids, Pageable pageable);

    @Query(value = "select bean from User bean where name like %?1% and phone like %?2% and department like %?3% and id in ?4")
    Page searchNoStateWithRole(String name, String phone, String department, List<Long> ids, Pageable pageable);

    @Query(value = "select bean from User bean where id in ?1 and state = 1")
    List<User> getUserInIds(List<Long> ids);

    @Query(value = "select bean from User bean where id in ?1")
    List<User> getAllUserInIds(List<Long> ids);//停用的用户也要搜索

    @Query(value = "select bean from User bean where department = ?1 and state=1")
    List<User> getUsersByDepartment(String department);

    @Query(value = "select bean from User bean where wechat_open_id = ?1")
    User getUserByOpenId(String openId);
}
