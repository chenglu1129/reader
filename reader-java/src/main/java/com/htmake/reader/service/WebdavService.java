package com.htmake.reader.service;

import com.google.gson.Gson;
import com.htmake.reader.entity.WebdavConfig;
import com.htmake.reader.utils.StorageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * WebDAV服务
 */
@Slf4j
@Service
public class WebdavService {

    private static final Gson GSON = new Gson();

    @Autowired
    private StorageHelper storageHelper;

    /**
     * 获取WebDAV配置
     */
    public WebdavConfig getConfig(String username) {
        String path = getConfigPath(username);
        File file = new File(path);

        if (!file.exists()) {
            return new WebdavConfig();
        }

        String json = storageHelper.readFile(path);
        if (json == null || json.isEmpty()) {
            return new WebdavConfig();
        }

        try {
            WebdavConfig config = GSON.fromJson(json, WebdavConfig.class);
            return config != null ? config : new WebdavConfig();
        } catch (Exception e) {
            log.error("解析WebDAV配置失败", e);
            return new WebdavConfig();
        }
    }

    /**
     * 保存WebDAV配置
     */
    public boolean saveConfig(WebdavConfig config, String username) {
        if (config == null) {
            return false;
        }

        String path = getConfigPath(username);
        String json = GSON.toJson(config);
        return storageHelper.writeFile(path, json);
    }

    /**
     * 备份数据到WebDAV（模拟实现）
     */
    public boolean backup(String username) {
        WebdavConfig config = getConfig(username);
        if (config == null || !config.getEnabled()) {
            log.warn("WebDAV未启用或配置无效");
            return false;
        }

        // TODO: 实现实际的WebDAV备份逻辑
        // 这里需要使用Sardine等WebDAV客户端库
        log.info("WebDAV备份功能待实现: {}", config.getUrl());

        // 更新同步时间
        config.setLastSyncTime(System.currentTimeMillis());
        saveConfig(config, username);

        return true;
    }

    /**
     * 从WebDAV恢复数据（模拟实现）
     */
    public boolean restore(String username) {
        WebdavConfig config = getConfig(username);
        if (config == null || !config.getEnabled()) {
            log.warn("WebDAV未启用或配置无效");
            return false;
        }

        // TODO: 实现实际的WebDAV恢复逻辑
        log.info("WebDAV恢复功能待实现: {}", config.getUrl());

        return true;
    }

    /**
     * 获取配置文件路径
     */
    private String getConfigPath(String username) {
        return storageHelper.getUserDataPath(username) + File.separator + "webdavConfig.json";
    }
}
