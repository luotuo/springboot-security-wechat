package com.luotuo.user.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by luotuo on 17-7-13.
 */
@Entity
@Table(name = "role_privilege")
public class RolePrivilege implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long role_id;
    private String role_name;
    private long privilege_id;
    private String privilege_name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRole_id() {
        return role_id;
    }

    public void setRole_id(long role_id) {
        this.role_id = role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public long getPrivilege_id() {
        return privilege_id;
    }

    public void setPrivilege_id(long privilege_id) {
        this.privilege_id = privilege_id;
    }

    public String getPrivilege_name() {
        return privilege_name;
    }

    public void setPrivilege_name(String privilege_name) {
        this.privilege_name = privilege_name;
    }
}
