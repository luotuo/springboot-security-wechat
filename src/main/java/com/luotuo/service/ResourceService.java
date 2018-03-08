package com.luotuo.service;

import com.luotuo.global.EncryptionAlgs;
import com.luotuo.user.entity.Resource;
import com.luotuo.user.repository.ResourceRepository;
import com.luotuo.user.repository.UserResourcesRepository;
import com.luotuo.utils.SpecificationFactory;
import com.luotuo.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional("secondTransactionManager")
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private UserResourcesRepository userResourcesRepository;

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

    public Object search(String resourceType,
                         String resourceName,
                         int page,
                         int size) throws Exception {
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
}
