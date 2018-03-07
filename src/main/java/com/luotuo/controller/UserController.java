package com.luotuo.controller;

import com.luotuo.user.entity.*;
import com.luotuo.global.result.Response;
import com.luotuo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;


/**
 * Created by luotuo on 17-7-4.
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 用户当前信息
     * 包括用户姓名，头像
     * */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/currentUserInfo")
    @ResponseBody
    public Response getAll(HttpServletRequest request) {
        User user = getUser();
        if (user == null)
            return errorResponse("无此用户", "无此用户");
        user.setPassword("");
        UserResponse userResponse = null;
        try {
            userResponse = userService.getUserInfo(user);
        } catch (Exception e) {
            return errorResponse("获取登录用户信息失败", e.toString());
        }
        return successResponse("登录用户信息", userResponse);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/edit")
    @ResponseBody
    public Response edit(@RequestParam(value = "id", required = true) String i,
                         HttpServletRequest request) {
        Long id = -1L;
        UserResponse user = null;
        try {
            id = Long.valueOf(i);
            if (id == 1)
                return errorResponse("超级管理员不允许编辑", null);
            user = userService.findUserById(id);
        } catch (Exception e) {
            return errorResponse("选择失败", e.toString());
        }
        return successResponse("选择成功", user);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getPrivileges")
    @ResponseBody
    public Response getPrivileges(@RequestParam(value = "id", required = true, defaultValue = "") String id,
                                 HttpServletRequest request) {
        long userId = 0;
        Iterable<UserPrivilege> userPrivileges = null;
        try {
            userId = Long.valueOf(id);
        } catch (Exception e) {
            return errorResponse("id有错误", null);
        }
        try {
            userPrivileges = userService.getUserPrivileges(userId);
        } catch (Exception e) {
            return errorResponse("获取失败", e.toString());
        }
        return successResponse("获取成功", userPrivileges);
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getRoles")
    @ResponseBody
    public Response getRoles(@RequestParam(value = "id", required = true, defaultValue = "") String id,
                                 HttpServletRequest request) {
        long userId = 0;
        Iterable<UserRole> userRoles = null;
        try {
            userId = Long.valueOf(id);
        } catch (Exception e) {
            return errorResponse("id有错误", null);
        }
        try {
            userRoles = userService.getUserRoles(userId);
        } catch (Exception e) {
            return errorResponse("获取失败", e.toString());
        }
        return successResponse("获取成功", userRoles);
    }

    @RequestMapping(method = {RequestMethod.POST}, value = "/setPassword")
    @ResponseBody
    public Response setPassword(@RequestParam(value = "oldPassword") String oldPassword,
                                @RequestParam(value = "newPassword") String newPassword,
                                HttpServletRequest request) {
        User user = getUser();
        try {
            if (oldPassword.equals(newPassword))
                return errorResponse("新旧密码必须不同", null);
            userService.setPassword(oldPassword, newPassword, user);
        } catch (Exception e) {
            return errorResponse("设置失败", e.toString());
        }
        return successResponse("设置成功", null);
    }

    @RequestMapping(method = {RequestMethod.POST}, value = "/resetPassword")
    @ResponseBody
    public Response setPassword(@RequestParam(value = "userId") String userId,
                                HttpServletRequest request) {
        try {
            userService.resetPassword(Long.valueOf(userId));
        } catch (Exception e) {
            return errorResponse("重置失败", e.toString());
        }
        return successResponse("重置成功", null);
    }

    @RequestMapping(method = {RequestMethod.POST}, value = "/bindWechat")
    @ResponseBody
    public Response bindWechat(HttpServletRequest request) {
        UserResponse userResponse = null;
        try {
            User user = userService.bindWechat(request, getUser());
            user.setPassword("");
            userResponse = userService.getUserInfo(user);
        } catch (Exception e) {
            return errorResponse("绑定失败", e.toString());
        }
        return successResponse("绑定成功", userResponse);
    }

    @RequestMapping(method = {RequestMethod.POST}, value = "/join")
    @ResponseBody
    public Response joinOpen(@RequestParam(value = "name") String name,
                             @RequestParam(value = "phone") String phone,
                             @RequestParam(value = "vCode") String vCode,
                             @RequestParam(value = "password") String password,
                             HttpServletRequest request) {
        UserResponse userResponse = null;
        try {
            User user = userService.join(name, phone, vCode, password, request);
            user.setPassword("");
            userResponse = userService.getUserInfo(user);
        } catch (Exception e) {
            return errorResponse("注册失败", e.toString());
        }
        return successResponse("注册成功", userResponse);
    }

    @RequestMapping(method = {RequestMethod.POST}, value = "/wechatJoin")
    @ResponseBody
    public Response wechatJoin(@RequestParam(value = "name") String name,
                               @RequestParam(value = "phone") String phone,
                               @RequestParam(value = "vCode") String vCode,
                               @RequestParam(value = "password") String password,
                               HttpServletRequest request) {
        UserResponse userResponse = null;
        try {
            User user = userService.wechatJoin(name, phone, vCode, password, getUser());
            user.setPassword("");
            userResponse = userService.getUserInfo(user);
        } catch (Exception e) {
            return errorResponse("绑定失败", e.toString());
        }
        return successResponse("绑定成功", userResponse);
    }
}
