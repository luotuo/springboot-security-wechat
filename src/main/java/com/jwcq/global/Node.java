package com.jwcq.global;

import java.util.List;
import java.util.Map;

/**
 * Created by luotuo on 17-7-4.
 */
public class Node {
    private Long id;
    private Long pid;
    Object value;
    private List<Node> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
