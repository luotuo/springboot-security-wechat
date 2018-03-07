package com.luotuo.service;

import com.luotuo.user.entity.Resource;
import com.luotuo.user.entity.User;
import com.luotuo.user.entity.UserResources;
import com.luotuo.user.repository.UserResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional("secondTransactionManager")
public class UserResourcesService {
    @Autowired
    private UserResourcesRepository userResourcesRepository;

    /**
     * @name 给一个人添加一些资源
     * @param resources
     * @param user
     * @throws Exception
     */
    public void addResourcesToUser(List<Resource> resources, User user) throws Exception {
        if (resources.isEmpty())
            throw new Exception("资源不能为空");
        if (user == null)
            throw new Exception("用户不能为空");
        List<UserResources> userResources = new ArrayList<>();
        for (Resource r : resources) {
            UserResources userResources1 = new UserResources(user, r);
            userResources.add(userResources1);
        }
        userResourcesRepository.save(userResources);
    }

    /**
     * @name 给一个资源添加一些人
     * @param users
     * @param resource
     * @throws Exception
     */
    public void addUsersToResource(List<User> users, Resource resource) throws Exception {
        if (users.isEmpty())
            throw new Exception("用户不能为空");
        if (resource == null)
            throw new Exception("资源不能为空");
        List<UserResources> userResources = new ArrayList<>();
        for (User u : users) {
            UserResources userResources1 = new UserResources(u, resource);
            userResources.add(userResources1);
        }
        userResourcesRepository.save(userResources);
    }

    /**
     * @name 给一个用户增加一个资源/给一个资源增加一个用户
     * @param user
     * @param resource
     * @throws Exception
     */
    public void addUserToResource(User user, Resource resource) throws Exception {
        if (user == null || resource == null)
            throw new Exception("用户和资源都不能为空");
        UserResources userResources = new UserResources(user, resource);
        userResourcesRepository.save(userResources);
    }

    /**
     * @name 删除一个用户资源
     * @param userResourceId
     * @throws Exception
     */
    public void deleteUserResource(Long userResourceId) throws Exception {
        userResourcesRepository.delete(userResourceId);
    }
}
