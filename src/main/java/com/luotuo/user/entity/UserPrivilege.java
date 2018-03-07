package com.luotuo.user.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by luotuo on 17-6-30.
 */
@Entity
@Table(name = "user_privilege")
public class UserPrivilege implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "user_id")
    private Long user_id;

    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "privilege_config_id")
    private Long privilege_id;

    private String privilege_name;


    public String getPrivilege_name() {
        return privilege_name;
    }

    public void setPrivilege_name(String privilege_name) {
        this.privilege_name = privilege_name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getPrivilege_id() {
        return privilege_id;
    }

    public void setPrivilege_id(Long privilege_id) {
        this.privilege_id = privilege_id;
    }
}
