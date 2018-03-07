package com.luotuo.service;

import com.luotuo.user.repository.UserResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("secondTransactionManager")
public class UserResourcesService {
    @Autowired
    private UserResourcesRepository userResourcesRepository;
}
