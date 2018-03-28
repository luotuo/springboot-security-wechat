package com.luotuo.service;

import com.luotuo.user.entity.Department;
import com.luotuo.user.entity.DepartmentResponse;
import com.luotuo.user.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luotuo on 17-7-3.
 */
@Service
@Transactional("secondTransactionManager")
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;
    /**
     * @description 获取部门的列表，排好序
     * @return
     */
    public List findAllTree() {
        // FIXME: We need a better way to build this tree in the future!
        // Find all roots first
        int level = 1;
        List<Department> temp = departmentRepository.findByLevel(level);
        List<DepartmentResponse> res = new ArrayList<>();
        if (temp.size() > 0) {
            for (Department p1 : temp) {
                DepartmentResponse dr = new DepartmentResponse();
                dr.set(p1, "");
                res.add(dr);
                List<Department> level2 = departmentRepository.findByPid(p1.getId());
                if (level2.size() > 0) {
                    for (Department p2 : level2) {
                        DepartmentResponse dr1 = new DepartmentResponse();
                        dr1.set(p2, p1.getName());
                        res.add(dr1);
                    }
                }
            }
        } else {
            level = 2;
            List<Department> level2 = departmentRepository.findByLevel(level);
            if (level2.size() > 0) {
                for (Department p2 : level2) {
                    DepartmentResponse dr1 = new DepartmentResponse();
                    dr1.set(p2, "");
                    res.add(dr1);
                }
            } else
                return level2;
        }

        return res;
    }
    /**
     * @description 获取所有部门
     * @return
     */
    public List findAllList() {
        List<Department> departments = departmentRepository.findAll();
        return departments;
    }
    /**
     * @description 通过id获取部门
     * @param id 部门id
     * @return
     */
    public Department findById(Long id) { return departmentRepository.findById(id); }
    /**
     * @description 通过id删除部门（会递归删除下一级所有部门）
     * @param id 部门id
     */
    public void deleteById(Long id) {
        // Delete all children
        List<Department> children = departmentRepository.findByPid(id);
        for (Department p : children) {
            List<Department> pp = departmentRepository.findByPid(p.getId());
            if (pp == null) {
                departmentRepository.delete(p.getId());
            } else {
                for (Department pChild : pp) {
                    deleteById(pChild.getId());
                }
            }
        }
        departmentRepository.delete(id);
    }
    /**
     * @description 保存部门
     * @param pid 部门pid
     * @param name 部门名称
     * @param level 部门级别
     * @return
     */
    public Department save(long pid, String name, int level) {
        Department department = new Department();
        department.setPid(pid);
        department.setLevel(level);
        department.setName(name);
        department = departmentRepository.save(department);
        return department;
    }
    /**
     * @description 更新部门
     * @param id 部门id
     * @param pid 部门pid
     * @param name 部门名称
     * @param level 部门级别
     * @return
     */
    public Department update(long id, long pid, String name, int level) {
        Department department = null;
        department = departmentRepository.findById(id);
        if (department == null)
            department = new Department();
        department.setPid(pid);
        department.setLevel(level);
        department.setName(name);
        departmentRepository.save(department);
        return department;
    }
    /**
     * @description 获取某个部门的所有子部门
     * @param pid 父部门id
     * @return
     */
    public List<Department> getByPid(long pid) {
        return departmentRepository.findByPid(pid);
    }
    /**
     * @description 通过名称获取部门
     * @param name 部门名称
     * @return
     */
    public List<Department> getByName(String name) {
        return departmentRepository.getByName(name);
    }
}
