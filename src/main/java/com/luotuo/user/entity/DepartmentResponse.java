package com.luotuo.user.entity;

/**
 * Created by luotuo on 17-7-25.
 */
public class DepartmentResponse {
    private long id;
    private long pid;
    private String name;
    private int level;
    private String pName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public void set(Department d, String pName) {
        this.id = d.getId();
        this.name = d.getName();
        this.pid = d.getPid();
        this.level = d.getLevel();
        this.pName = pName;
    }
}
