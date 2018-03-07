package com.jwcq.controller;

import com.jwcq.service.UserResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userResources")
public class UserResourcesController extends BaseController{
    @Autowired
    private UserResourcesService userResourcesService;
}
