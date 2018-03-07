package com.jwcq.user.entity;

import com.jwcq.global.EncryptionAlgs;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "user_resources")
public class UserResources {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private long userId;
    /**
     * 资源唯一id，由资源类型+资源原始id经过md5得到
     */
    @Column(name = "resource_id")
    private String resourceId;
    /**
     * 资源原始id
     */
    @Column(name = "resource_origin_id", columnDefinition = "not null default 0")
    private long resourceOriginId;
    /**
     * 资源类型
     */
    @Column(name = "resource_type", columnDefinition = "not null default ''")
    private String resourceType;

    public UserResources() {}

    public UserResources(long userId, long resourceOriginId, String resourceType) {
        this.userId = userId;
        this.resourceOriginId = resourceOriginId;
        this.resourceType = resourceType;
        this.resourceId = EncryptionAlgs.getMD5(resourceType + resourceOriginId);
    }

    public UserResources(long userId, Resource resource) {
        this.userId = userId;
        this.resourceId = resource.getResourceId();
        this.resourceType = resource.getResourceType();
        this.resourceOriginId = resource.getResourceOriginId();
    }
}
