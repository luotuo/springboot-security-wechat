package com.luotuo.entity;

import lombok.Data;

/**
 * Created by luotuo on 17-7-18.
 */
@Data
public class PlatformAndMenu {
    /**
     * id
     */
    private long id;
    /**
     * 平台
     */
    private String platform;
    /**
     * 菜单
     */
    private String menu;
}
