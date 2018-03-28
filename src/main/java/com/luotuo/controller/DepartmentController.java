package com.luotuo.controller;

import com.luotuo.global.result.Response;
import com.luotuo.service.DepartmentService;
import com.luotuo.user.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by luotuo on 17-7-4.
 */
@Controller
@RequestMapping("/department")
public class DepartmentController extends BaseController {
    @Autowired
    private DepartmentService departmentService;

    /**
     * @name 获取所有部门，已排好序的结构
     * @param request
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAll")
    @ResponseBody
    public Response getAll(HttpServletRequest request) {
        List result = departmentService.findAllTree();
        return successResponse("处理成功", result);
    }

    /**
     * @name 获取所有部门列表
     * @param request
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAllList")
    @ResponseBody
    public Response getAllList(HttpServletRequest request) {
        List result = departmentService.findAllList();
        return successResponse("处理成功", result);
    }

    /**
     * @name 新增部门
     * @param pid
     * @param name
     * @param l
     * @param request
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/add")
    @ResponseBody
    public Response add(@RequestParam(value = "pid", required = true) String pid,
                        @RequestParam(value = "name", required = true) String name,
                        @RequestParam(value = "level", required = true) String l,
                        HttpServletRequest request) {
        int pId = -1;
        int level = -1;
        try {
            pId = Integer.valueOf(pid);
            level = Integer.valueOf(l);
        } catch (Exception e) {
            System.out.println(e.toString());
            return errorResponse("数据类型有误！", e.toString());
        }
        Department res = departmentService.save(pId, name, level);
        if (res != null)
            return successResponse("添加成功！", res);
        else
            return errorResponse("数据类型有误！", null);
    }

    /**
     * @name 获取部门
     * @param i
     * @param request
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/edit")
    @ResponseBody
    public Response edit(@RequestParam(value = "id", required = true) String i,
                         HttpServletRequest request) {
        Long id = -1L;
        Department department = null;
        try {
            id = Long.valueOf(i);
            department = departmentService.findById(id);
        } catch (Exception e) {
            return errorResponse("选择失败", e.toString());
        }
        return successResponse("选择成功", department);
    }

    /**
     * @name 更新部门
     * @param i
     * @param pid
     * @param name
     * @param l
     * @param request
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/update")
    @ResponseBody
    public Response update(@RequestParam(value = "id", required = true) String i,
                           @RequestParam(value = "pid", required = true) String pid,
                           @RequestParam(value = "name", required = true) String name,
                           @RequestParam(value = "level", required = true) String l,
                         HttpServletRequest request) {
        Long id = -1L;
        long pId = -1;
        int level = -1;
        try {
            id = Long.valueOf(i);
            pId = Long.valueOf(pid);
            level = Integer.valueOf(l);
        } catch (Exception e) {
            return errorResponse("选择失败", e.toString());
        }
        Department department = departmentService.update(id, pId, name, level);
        if (department != null)
            return successResponse("修改成功！", department);
        else
            return errorResponse("数据类型有误！", null);
    }

    /**
     * @name 删除部门
     * @param i
     * @param request
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/delete")
    @ResponseBody
    public Response delete(@RequestParam(value = "id", required = true) String i,
                           HttpServletRequest request) {
        Long id = -1L;
        try {
            id = Long.valueOf(i);
            departmentService.deleteById(id);
        } catch (Exception e) {
            System.out.println(e.toString());
            return errorResponse("删除失败", e.toString());
        }
        return successResponse("删除成功", null);
    }

    /**
     * @name 获取某个部门的子部门
     * @param i
     * @param request
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getByPid")
    @ResponseBody
    public Response getByPid(@RequestParam(value = "pid", required = true) String i,
                           HttpServletRequest request) {
        Long pid = -1L;
        List<Department> res = null;
        try {
            pid = Long.valueOf(i);
            res = departmentService.getByPid(pid);
        } catch (Exception e) {
            System.out.println(e.toString());
            return errorResponse("获取失败", e.toString());
        }
        return successResponse("获取成功", res);
    }
}
