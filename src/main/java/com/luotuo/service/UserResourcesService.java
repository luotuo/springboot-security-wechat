package com.luotuo.service;

import com.luotuo.user.repository.UserResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserResourcesService {
    @Autowired
    private UserResourcesRepository userResourcesRepository;
}
