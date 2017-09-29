package com.jwcq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by luotuo on 17-9-28.
 */
@Controller
public class LoginController {
    @RequestMapping(method= {RequestMethod.GET,RequestMethod.POST}, value = "/")
    public String login(HttpServletRequest request) {
        return "index";
    }
}