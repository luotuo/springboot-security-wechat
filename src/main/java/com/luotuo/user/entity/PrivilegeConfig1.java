package com.luotuo.user.entity;

/**
 * Created by luotuo on 17-7-24.
 */
public class PrivilegeConfig1 {
    private long id;
    private long pid;
    private int level;
    private String level_str;
    private String name;
    private String type;
    private String value;
    private String url;
    private int state;
    private String state_str;
    private String platform;

    private Boolean checked;
    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLevel_str() {
        return level_str;
    }

    public void setLevel_str(String level_str) {
        this.level_str = level_str;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getState_str() {
        return state_str;
    }

    public void setState_str(String state_str) {
        this.state_str = state_str;
    }

    public void set(PrivilegeConfig p, Boolean checked) {
        this.id = p.getId();
        this.pid = p.getPid();
        this.level = p.getLevel();
        this.level_str = p.getLevel_str();
        this.name = p.getName();
        this.type = p.getType();
        this.value = p.getValue();
        this.url = p.getUrl();
        this.state = p.getState();
        this.state_str = p.getState_str();
        this.platform = p.getPlatform();
        this.checked = checked;
    }
}
