package com.jwcq.controller;
import com.jwcq.config.Global;
import com.jwcq.custom.UserInfo;
import com.jwcq.global.result.Response;
import com.jwcq.service.UserService;
import com.jwcq.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * Created by luotuo on 17-6-1.
 */
@Controller
public class BaseController {
    //默认请求的分页信息，具体的值在Global中设置。缺省值是20项每页，第0页。
    Sort sort = new Sort(Sort.Direction.DESC, "id");
    public PageRequest pageRequest = new PageRequest(Global.DEFAULT_PAGE_NUM, Global.DEFAULT_PAGE_SIZE, sort);
    //日志系统
    public final Logger mlogger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    UserService userService;


    public Long getUserId() {
        try {
            Object userInfo = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            if (userInfo instanceof Long)
                return (Long)userInfo;
            return ((UserInfo)userInfo).getId();
        } catch (Exception e) {
            return 0L;
        }
    }

    //返回请求中的参数值
    public String getParam(HttpServletRequest request, String name) {
        if (name == null || name == "") return null;
        String para = ServletRequestUtils.getStringParameter(request, name, null);
        return para;
    }

    //获取正确
    public Response successResponse(String message, Object result) {
        if (message == null) message = "处理成功";
        Response response = new Response();
        response.setSuccess(Response.SUCCEED);
        response.setMessage(message);
        if (result == null)
            result = 1;
        response.setResult(result);
        return response;
    }

    //出现错误
    public Response errorResponse(String message, Object result) {
        if (message == null) message = "处理失败";
        Response response = new Response();
        response.setSuccess(Response.ERROR);
        response.setMessage(message);
        if (result == null)
            result = 0;
        response.setResult(result);
        return response;
    }

    /**
     * 获取当前登录的用户信息
     */
    public User getUser() {
        long userId = getUserId();
        if (userId == 0)
            return null;
        User user = userService.getUserById(userId);
        return user;
    }
}





