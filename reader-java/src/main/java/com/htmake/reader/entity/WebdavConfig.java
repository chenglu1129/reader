package com.htmake.reader.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebDAV配置实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebdavConfig {

    /**
     * WebDAV服务器地址
     */
    private String url = "";

    /**
     * 用户名
     */
    private String username = "";

    /**
     * 密码
     */
    private String password = "";

    /**
     * 备份目录
     */
    private String backupDir = "legado";

    /**
     * 是否启用
     */
    private Boolean enabled = false;

    /**
     * 上次同步时间
     */
    private Long lastSyncTime = 0L;
}
