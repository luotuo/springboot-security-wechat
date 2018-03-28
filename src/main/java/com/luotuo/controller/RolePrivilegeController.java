package com.luotuo.controller;

import com.luotuo.service.RolePrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by luotuo on 17-7-13.
 */
@Controller
@RequestMapping("/rolePrivilege")
public class RolePrivilegeController extends BaseController {

    @Autowired
    private RolePrivilegeService rolePrivilegeService;
}
