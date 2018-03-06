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
    /**
     * 如果使用HTML head中
     * <html><head><meta http-equiv=\"refresh\" content=\"1;url=%s\"></head><body></body></html>
     * 增加ResponseBody注解
     */
    public String login(HttpServletRequest request) {
        return "index";
    }
}