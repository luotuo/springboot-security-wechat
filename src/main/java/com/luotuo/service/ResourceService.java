package com.luotuo.service;

import com.luotuo.entity.BaseResource;
import com.luotuo.global.EncryptionAlgs;
import com.luotuo.user.entity.Resource;
import com.luotuo.user.repository.ResourceRepository;
import com.luotuo.user.repository.UserResourcesRepository;
import com.luotuo.utils.PackageUtil;
import com.luotuo.utils.SpecificationFactory;
import com.luotuo.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional("secondTransactionManager")
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private UserResourcesRepository userResourcesRepository;

    @Autowired
    ApplicationContext context;

    public Resource add(String resourceType, Long resourceOriginId, String resourceName) throws Exception {
        if (StringUtils.isNotBlank(resourceType) || resourceOriginId == null)
            throw new Exception("资源类型或资源原始id都不能为空");
        Resource resource = new Resource(resourceType, resourceOriginId, resourceName);
        return resourceRepository.save(resource);
    }

    @Transactional
    public void delete(String resourceType, Long resourceOriginId) throws Exception {
        if (StringUtils.isNotBlank(resourceType) || resourceOriginId == null)
            throw new Exception("资源类型或资源原始id都不能为空");
        String resourceId = EncryptionAlgs.getMD5(resourceType + resourceOriginId.toString());
        Resource resource = resourceRepository.findByResourceId(resourceId);
        if (resource == null)
            throw new Exception("无此资源");
        resourceRepository.delete(resource.getId());
        /**
         * delete all user resources
         */
        userResourcesRepository.deleteByResourceId(resourceId);
    }

    public Object findById(Long id) throws Exception {
        Resource resource = resourceRepository.findOne(id);
        if (resource == null)
            throw new Exception("无此id资源");
        return resource;
    }

    /**
     * @name 搜索资源
     * @param resourceType 资源类型
     * @param resourceName 资源名称
     * @param page 分页的页码
     * @param size 每页的数量
     * @return
     * @throws Exception
     */
    public Object search(String resourceType,
                         String resourceName,
                         int page,
                         int size) throws Exception {
        addResourcesAuto();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(page, size, sort);
        Specifications<Resource> conditions = null;
        if (StringUtils.isNotBlank(resourceName)) {
            if (conditions == null)
                conditions = Specifications.where(SpecificationFactory.containsLike("resource_name", resourceName));
            else
                conditions = conditions.and(SpecificationFactory.containsLike("resource_name", resourceName));
        }
        if (StringUtils.isNotBlank(resourceType)) {
            if (conditions == null)
                conditions = Specifications.where(SpecificationFactory.containsLike("resource_type", resourceType));
            else
                conditions = conditions.and(SpecificationFactory.containsLike("resource_type", resourceType));
        }
        Page<Resource> page1 = null;
        if (conditions == null)
            page1 = resourceRepository.findAll(pageRequest);
        else
            page1 = resourceRepository.findAll(conditions, pageRequest);
        return page1;
    }

    /**
     * 自动获取系统所有资源
     * 需要每一个资源entity继承自BaseResource且对应的service继承BaseResourceService
     * @throws Exception
     */
    private void addResourcesAuto() throws Exception {
        List<Class<?>> classes = PackageUtil.getClass("com.luotuo", true);
        if (classes.isEmpty())
            return;
        List<Resource> resources = resourceRepository.findAll();
        Map<String, Resource> resourceMap = new HashMap<>();
        for (Resource r : resources) {
            resourceMap.put(r.getResourceId(), r);
        }
        String []beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            System.out.println("bean name == " + beanName);
        }
        for (Class c : classes) {
            Class superClass = c.getSuperclass();
            if (superClass == null)
                continue;
            if (!"com.luotuo.entity.BaseResource".equals(superClass.getName()))
                continue;
            String []cPackage = c.getName().split("\\.");
            if (cPackage.length <= 0)
                continue;
            String serviceName = cPackage[cPackage.length -1] + "Service";
            Object objBean = context.getBean(StringUtils.toLowerCaseFirstOne(serviceName));
            Method serviceFindAllMethod = objBean.getClass().getMethod("findAll");
            if (serviceFindAllMethod == null)
                continue;
            List<BaseResource> resourceList = (List<BaseResource>)serviceFindAllMethod.invoke(objBean);
            for (BaseResource r : resourceList) {
                if (resourceMap.containsKey(r.getResourceId())) {
                    if (r.getName().equals(resourceMap.get(r.getResourceId())))
                        continue;
                    resourceMap.get(r.getResourceId()).setResourceName(r.getName());
                } else {
                    Resource resource = new Resource(r);
                    resources.add(resource);
                }
            }
        }
        if (!resources.isEmpty())
            resourceRepository.save(resources);
    }
}
