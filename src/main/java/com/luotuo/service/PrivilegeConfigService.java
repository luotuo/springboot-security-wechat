package com.luotuo.service;

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

    public List<PrivilegeConfig> findAll() {
        List<PrivilegeConfig> privilegeConfigs = privilegeConfigRepository.findAll();
        return privilegeConfigs;
    }

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


    public Iterable<PrivilegeConfig> findAll(int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(page, pageSize, sort);
        Iterable<PrivilegeConfig> privilegeConfigs = privilegeConfigRepository.findAll(pageRequest);
        return privilegeConfigs;
    }

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

    private int getLevelByType(String type) {
        int res = 1;
        if (type.equals("目录")) res = 1;
        else if (type.equals("菜单")) res = 2;
        else if (type.equals("按钮")) res = 3;
        return res;
    }

    private String getLevelStr(Integer level) {
        return level.toString() + "级";
    }

    private String getStateStr(Integer state) {
        if (state == 1) return "正常";
        else if (state == 0) return "停用";
        else return "停用";
    }

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


    public PrivilegeConfig findById(Long id) {
        return privilegeConfigRepository.findById(id);
    }

    public void deleteById(Long id) {
        // Delete all children
        List<PrivilegeConfig> children = privilegeConfigRepository.findByPid(id);
        for (PrivilegeConfig p : children) {
            List<PrivilegeConfig> pp = privilegeConfigRepository.findByPid(p.getId());
            if (pp.size() == 0) {
                privilegeConfigRepository.delete(p.getId());
            } else {
                for (PrivilegeConfig pChild : pp) {
                    deleteById(pChild.getId());
                }
            }
        }
        privilegeConfigRepository.delete(id);
    }

    public Iterable<PrivilegeConfig> saveNew(List privileges) {
        return privilegeConfigRepository.save(privileges);
    }

    private int stateStr2Int(String state) {
        int res = 0;
        if (state.equals("正常"))
            res = 1;
        return res;
    }

    private int levelStr2Int(String state) {
        int res = 0;
        if (state.equals("1级")) res = 1;
        else if (state.equals("2级")) res = 2;

        return res;
    }

    public List<PrivilegeConfig> findByPid(long pid) {
        return privilegeConfigRepository.findByPid(pid);
    }


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

    public List<PrivilegeConfig> getByIds(List<Long> ids) {
        return privilegeConfigRepository.findByIds(ids);
    }

    public List<PrivilegeConfig> getByIdsAndLevel(List<Long> ids, int level) {
        return privilegeConfigRepository.findByIdsAndLevel(ids, level);
    }

    public List<PrivilegeConfig> getByIdsAndPid(List<Long> ids, Long pid) {
        return privilegeConfigRepository.findByIdsAndPid(ids, pid);
    }
}
