package com.luotuo.service;

import com.luotuo.repository.TestResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class TestResourceService implements BaseResourceService {
    @Autowired
    private TestResourceRepository testResourceRepository;
    @Override
    public Object findAll() throws Exception {
        return testResourceRepository.findAll();
    }
}
