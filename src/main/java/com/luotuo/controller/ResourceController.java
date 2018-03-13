package com.luotuo.controller;

import com.luotuo.global.result.Response;
import com.luotuo.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/resource")
public class ResourceController extends BaseController {
    @Autowired
    private ResourceService resourceService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/search")
    @ResponseBody
    public Response search(@RequestParam(value = "resourceType", required = false, defaultValue = "") String resourceType,
                           @RequestParam(value = "resourceName", required = false, defaultValue = "") String resourceName,
                           @RequestParam(value = "page", required = false, defaultValue = "0") String page,
                           @RequestParam(value = "size", required = false, defaultValue = "20") String size,
                           HttpServletRequest request) {
        Object res = null;
        try {
            res = resourceService.search(resourceType, resourceName, Integer.valueOf(page), Integer.valueOf(size));
        } catch (Exception e) {
            return errorResponse("查询失败" + e.getMessage(), e.toString());
        }
        return successResponse("查询成功", res);
    }
}
