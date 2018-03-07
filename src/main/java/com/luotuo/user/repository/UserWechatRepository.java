package com.luotuo.user.repository;

import com.luotuo.user.entity.User;
import com.luotuo.user.entity.UserWechat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Table;

/**
 * Created by luotuo on 17-9-22.
 */
@Table(name = "user_wechat")
public interface UserWechatRepository extends JpaRepository<UserWechat, Long> {
    @Query(value = "select bean from UserWechat bean where openid = ?1")
    UserWechat getByOpenId(String openId);
}
