package com.luotuo.service;

import com.luotuo.entity.PlatformAndMenu;
import com.luotuo.user.entity.PrivilegeConfig1;
import com.luotuo.user.entity.RolePrivilege;
import com.luotuo.user.entity.UserPrivilege;
import com.luotuo.user.repository.PrivilegeConfigRepository;
import com.luotuo.user.entity.PrivilegeConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by luotuo on 17-6-30.
 */
@Service
@Transactional("secondTransactionManager")
public class PrivilegeConfigService {
    @Autowired
    private PrivilegeConfigRepository privilegeConfigRepository;

    @Autowired
    private UserPrivilegeService userPrivilegeService;

    @Autowired
    private RolePrivilegeService rolePrivilegeService;
    /**
     * @description 获取所有权利列表
     * @return
     */
    public List<PrivilegeConfig> findAll() {
        List<PrivilegeConfig> privilegeConfigs = privilegeConfigRepository.findAll();
        return privilegeConfigs;
    }
    /**
     * @description 获取权限列表排好序的结构
     * @return
     */
    public List findAllTree() {
        // FIXME: We need a better way to build this tree in the future!
        // Find all roots first
        int level = 1;
        List<PrivilegeConfig> temp = privilegeConfigRepository.findByLevel(level);
        List<PrivilegeConfig> res = new ArrayList<>();
        if (temp.size() > 0) {
            for (PrivilegeConfig p1 : temp) {
                res.add(p1);
                List<PrivilegeConfig> level2 = privilegeConfigRepository.findByPid(p1.getId());
                if (level2.size() > 0) {
                    for (PrivilegeConfig p2 : level2) {
                        res.add(p2);
                        List<PrivilegeConfig> level3 = privilegeConfigRepository.findByPid(p2.getId());
                        if (level3.size() > 0) {
                            for (PrivilegeConfig p3 : level3) {
                                res.add(p3);
                            }
                        }
                    }
                }
            }
        } else {
            level = 2;
            temp = privilegeConfigRepository.findByLevel(level);
            if (temp.size() > 0) {
                for (PrivilegeConfig p2 : temp) {
                    res.add(p2);
                    List<PrivilegeConfig> level3 = privilegeConfigRepository.findByPid(p2.getId());
                    if (level3.size() > 0) {
                        for (PrivilegeConfig p3 : level3) {
                            res.add(p3);
                        }
                    }
                }

            } else {
                level = 3;
                temp = privilegeConfigRepository.findByLevel(level);
                return temp;
            }
        }
        return res;
    }

    /**
     * @description 获取权限列表的分页显示结构，未使用
     * @param page 页页码
     * @param pageSize 每页数量
     * @return
     */
    public Iterable<PrivilegeConfig> findAll(int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(page, pageSize, sort);
        Iterable<PrivilegeConfig> privilegeConfigs = privilegeConfigRepository.findAll(pageRequest);
        return privilegeConfigs;
    }
    /**
     * @description 保存权限配置
     * @param request
     * @return
     * @throws Exception
     */
    public PrivilegeConfig save(HttpServletRequest request) throws Exception {
        PrivilegeConfig privilegeConfig = new PrivilegeConfig();
        try {
            long pid = Long.valueOf(request.getParameter("pid"));
            String name = request.getParameter("name");
            String type = request.getParameter("type");
            String value = request.getParameter("value");
            String url = request.getParameter("url");
            int level = getLevelByType(type);
            //int state = Integer.valueOf(request.getParameter("state"));
            int state = 1;
            String levelStr = getLevelStr(level);
            String stateStr = getStateStr(state);
            String platform = request.getParameter("platform");
            privilegeConfig.setLevel(level);
            privilegeConfig.setLevel_str(levelStr);
            privilegeConfig.setName(name);
            privilegeConfig.setPid(pid);
            privilegeConfig.setPlatform(platform);
            privilegeConfig.setState(state);
            privilegeConfig.setState_str(stateStr);
            privilegeConfig.setType(type);
            privilegeConfig.setUrl(url);
            privilegeConfig.setValue(value);
        } catch (Exception e) {
            throw e;
        }
        privilegeConfig = privilegeConfigRepository.save(privilegeConfig);
        return privilegeConfig;
    }
    /**
     * @description 通过类型获取权限level
     * @param type 类型
     * @return
     */
    private int getLevelByType(String type) {
        int res = 1;
        if (type.equals("目录")) res = 1;
        else if (type.equals("菜单")) res = 2;
        else if (type.equals("按钮")) res = 3;
        return res;
    }
    /**
     * @description 获取level的字符形式
     * @param level
     * @return
     */
    private String getLevelStr(Integer level) {
        return level.toString() + "级";
    }
    /**
     * @description 获取状态的字符形式
     * @param state
     * @return
     */
    private String getStateStr(Integer state) {
        if (state == 1) return "正常";
        else if (state == 0) return "停用";
        else return "停用";
    }
    /**
     * @description 更新一个权限配置
     * @param request
     * @param id 权限配置的id
     * @return
     * @throws Exception
     */
    public PrivilegeConfig update(HttpServletRequest request, long id) throws Exception {
        PrivilegeConfig privilegeConfig = privilegeConfigRepository.findById(id);
        if (privilegeConfig == null)
            privilegeConfig = new PrivilegeConfig();
        try {
            long pid = Long.valueOf(request.getParameter("pid"));
            String name = request.getParameter("name");
            String type = request.getParameter("type");
            String value = request.getParameter("value");
            String url = request.getParameter("url");
            int level = getLevelByType(type);
            //int state = Integer.valueOf(request.getParameter("state"));
            int state = 1;
            String levelStr = getLevelStr(level);
            String stateStr = getStateStr(state);
            String platform = request.getParameter("platform");
            privilegeConfig.setPid(pid);
            privilegeConfig.setLevel_str(levelStr);
            privilegeConfig.setLevel(level);
            privilegeConfig.setName(name);
            privilegeConfig.setType(type);
            privilegeConfig.setValue(value);
            privilegeConfig.setUrl(url);
            privilegeConfig.setState_str(stateStr);
            privilegeConfig.setState(state);
            privilegeConfig.setPlatform(platform);
            privilegeConfigRepository.save(privilegeConfig);
        } catch (Exception e) {
            throw e;
        }
        return privilegeConfig;
    }

    /**
     * @description 通过id找到权限配置
     * @param id 权限配置id
     * @return
     */
    public PrivilegeConfig findById(Long id) {
        return privilegeConfigRepository.findById(id);
    }
    /**
     * @description 通过id删除权限配置，会递归
     * @param id 权限配置id
     */
    public void deleteById(Long id) {
        // Delete all children
        List<PrivilegeConfig> children = privilegeConfigRepository.findByPid(id);
        for (PrivilegeConfig p : children) {
            List<PrivilegeConfig> pp = privilegeConfigRepository.findByPid(p.getId());
            if (pp.size() == 0) {
                /**
                 * 删除目前已经配置的所有权限
                 */
                userPrivilegeService.deleteByPrivilegeId(p.getId());
                rolePrivilegeService.deleteByPrivilegeId(p.getId());
                privilegeConfigRepository.delete(p.getId());
            } else {
                for (PrivilegeConfig pChild : pp) {
                    deleteById(pChild.getId());
                }
            }
        }
        privilegeConfigRepository.delete(id);
    }

    /**
     * @description 获取所有平台和菜单
     * @return
     */
    public List<PlatformAndMenu> findPlatformsAndMenus () {
        List<PlatformAndMenu> res = new ArrayList<>();
        List<Object[]> obj = privilegeConfigRepository.findPlatformsAndMenus();
        if (obj.size() <= 0)
            return null;
        for (Object[] o : obj) {
            PlatformAndMenu platformAndMenu = new PlatformAndMenu();
            platformAndMenu.setId(Long.valueOf(o[0].toString()));
            platformAndMenu.setPlatform(o[1].toString());
            platformAndMenu.setMenu(o[2].toString());
            res.add(platformAndMenu);
        }
        return res;
    }
    /**
     * @description 保存权限列表，未使用
     * @param privileges 权限列表
     * @return
     */
    public Iterable<PrivilegeConfig> saveNew(List privileges) {
        return privilegeConfigRepository.save(privileges);
    }
    /**
     * @description 状态字符串转换为数值
     * @param state 状态字符串
     * @return
     */
    private int stateStr2Int(String state) {
        int res = 0;
        if (state.equals("正常"))
            res = 1;
        return res;
    }
    /**
     * @description 级别字符串转换为数值
     * @param state 级别字符串
     * @return
     */
    private int levelStr2Int(String state) {
        int res = 0;
        if (state.equals("1级")) res = 1;
        else if (state.equals("2级")) res = 2;

        return res;
    }
    /**
     * @description 找到所有pid相同的权限列表
     * @param pid pid
     * @return
     */
    public List<PrivilegeConfig> findByPid(long pid) {
        return privilegeConfigRepository.findByPid(pid);
    }

    /**
     * @description 获取某个用户某pid下的所有权限值
     * @param pid pid
     * @param userId 用户id
     * @return
     */
    public List<PrivilegeConfig1> getByPidAndUserId(long pid, long userId) {
        List<UserPrivilege> userPrivileges = userPrivilegeService.findByUserId(userId);
        List<PrivilegeConfig> privilegeConfigs = privilegeConfigRepository.findByPid(pid);
        List<PrivilegeConfig1> res = new ArrayList<>();
        for (PrivilegeConfig p : privilegeConfigs) {
            PrivilegeConfig1 privilegeConfig1 = new PrivilegeConfig1();
            privilegeConfig1.set(p, false);
            for (UserPrivilege u : userPrivileges) {
                if (p.getId() == u.getPrivilege_id())
                    privilegeConfig1.setChecked(true);
            }
            res.add(privilegeConfig1);
        }
        return res;
    }
    /**
     * @description 获取某个角色某pid下的所有权限值
     * @param pid pid
     * @param roleId 角色id
     * @return
     */
    public List<PrivilegeConfig1> getByPidAndRoleId(long pid, long roleId) {
        List<RolePrivilege> rolePrivileges = rolePrivilegeService.findByRoleId(roleId);
        List<PrivilegeConfig> privilegeConfigs = privilegeConfigRepository.findByPid(pid);
        List<PrivilegeConfig1> res = new ArrayList<>();
        for (PrivilegeConfig p : privilegeConfigs) {
            PrivilegeConfig1 privilegeConfig1 = new PrivilegeConfig1();
            privilegeConfig1.set(p, false);
            for (RolePrivilege r : rolePrivileges) {
                if (p.getId() == r.getPrivilege_id())
                    privilegeConfig1.setChecked(true);
            }
            res.add(privilegeConfig1);
        }
        return res;
    }
    /**
     * @description 通过id列表的获取权限
     * @param ids 权限id列表
     * @return
     */
    public List<PrivilegeConfig> getByIds(List<Long> ids) {
        return privilegeConfigRepository.findByIds(ids);
    }
    /**
     * @description 通过id列表和级别获取权限列表
     * @param ids 权限id列表
     * @param level 级别
     * @return
     */
    public List<PrivilegeConfig> getByIdsAndLevel(List<Long> ids, int level) {
        return privilegeConfigRepository.findByIdsAndLevel(ids, level);
    }
    /**
     * @description 通过id列表和pid获取权限列表
     * @param ids 权限id列表
     * @param pid pid
     * @return
     */
    public List<PrivilegeConfig> getByIdsAndPid(List<Long> ids, Long pid) {
        return privilegeConfigRepository.findByIdsAndPid(ids, pid);
    }
}
