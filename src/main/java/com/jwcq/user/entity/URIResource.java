package com.jwcq.user.entity;

import javax.persistence.*;

/**
 * Created by luotuo on 17-6-29.
 */
@Entity
@Table(name = "uri_resource")
public class URIResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uri;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
