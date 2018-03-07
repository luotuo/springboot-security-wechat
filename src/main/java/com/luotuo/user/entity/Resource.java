package com.luotuo.user.entity;

import com.luotuo.global.EncryptionAlgs;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "resource")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 资源类型
     */
    @Column(name = "resource_type", columnDefinition = "not null default ''")
    private String resourceType;
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
     * 资源名称
     */
    @Column(name = "resource_name")
    private String resourceName;

    public Resource() {}

    public Resource(String type, long resourceOriginId, String name) {
        this.resourceOriginId = resourceOriginId;
        this.resourceType = type;
        this.resourceId = EncryptionAlgs.getMD5(type + resourceOriginId);
        this.resourceName = name;
    }
}
