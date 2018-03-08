package com.luotuo.controller;

import com.luotuo.global.result.Response;
import com.luotuo.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/resource")
public class ResourceController extends BaseController {
    @Autowired
    private ResourceService resourceService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/search")
    @ResponseBody
    public Response search(HttpServletRequest request) {
        Object res = null;
        try {

        } catch (Exception e) {
            return errorResponse("查询失败" + e.getMessage(), e.toString());
        }
        return successResponse("查询成功", res);
    }
}
