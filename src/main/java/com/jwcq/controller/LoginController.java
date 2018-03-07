package com.jwcq.controller;

import com.jwcq.user.entity.User;
import com.jwcq.utils.SystemConfig;
import org.codehaus.jackson.map.Serializers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by luotuo on 17-9-28.
 */
@Controller
public class LoginController extends BaseController {
    /**
     * 如果使用HTML head中
     * <html><head><meta http-equiv=\"refresh\" content=\"1;url=%s\"></head><body></body></html>
     * 增加ResponseBody注解
     */
//    @RequestMapping(method= {RequestMethod.GET,RequestMethod.POST}, value = "/")
//    public String login(HttpServletRequest request) {
//        return "index";
//    }

    /**
     * 微信登录成功后访问
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(method= {RequestMethod.GET,RequestMethod.POST}, value = "/")
    public String login(HttpServletRequest request) {
        User user = getUser();
        String responseStr = "<html><head><meta http-equiv=\"refresh\" content=\"1;url=%s\"></head><body></body></html>";
        String param = SystemConfig.getProperty("app.server.base.url") +"/view/login.html";
        if (user != null) {
            param = SystemConfig.getProperty("callback.path.project");
        }
        responseStr = String.format(responseStr, param);
        System.out.println("log ----: "+responseStr);
        return responseStr;
    }

}