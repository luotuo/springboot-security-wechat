package com.jwcq.controller;

import com.google.common.io.Files;
import com.jwcq.MyExceptions.NullException;
import com.jwcq.config.Global;
import com.jwcq.service.PrivilegeConfigService;
import com.jwcq.service.UserPrivilegeService;
import com.jwcq.service.UserRoleService;
import com.jwcq.user.entity.*;
import com.jwcq.global.result.Response;
import com.jwcq.service.UserService;
import com.jwcq.MyExceptions.AlreadyExistException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by luotuo on 17-7-4.
 */
@Controller
@RequestMapping("/user")
@Api(description = "用户管理")
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

    @ApiOperation(value = "编辑用户", notes = "编辑一个用户")
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

    @ApiOperation(value = "获取用户权限", notes = "获取用户的所有权限")
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


    @ApiOperation(value = "获取用户角色", notes = "获取用户的所有角色")
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

    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
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

    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
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

    @ApiOperation(value = "绑定微信接口", notes = "绑定微信接口")
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

    @ApiOperation(value = "用户注册接口", notes = "用户注册接口")
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

    @ApiOperation(value = "微信扫描后用户注册接口", notes = "微信扫描后用户注册接口")
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
