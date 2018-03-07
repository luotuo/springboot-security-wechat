package com.jwcq.service;

import com.jwcq.user.repository.UserResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserResourcesService {
    @Autowired
    private UserResourcesRepository userResourcesRepository;
}
