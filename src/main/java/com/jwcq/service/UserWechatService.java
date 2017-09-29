package com.jwcq.service;

import com.jwcq.user.entity.UserWechat;
import com.jwcq.user.repository.UserWechatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by luotuo on 17-9-22.
 */
@Service
@Transactional("secondTransactionManager")
public class UserWechatService {
    @Resource
    private UserWechatRepository userWechatRepository;

    public UserWechat save(UserWechat userWechat) {
        return userWechatRepository.save(userWechat);
    }

    public UserWechat getByOpenId(String openId) {
        return userWechatRepository.getByOpenId(openId);
    }
}
