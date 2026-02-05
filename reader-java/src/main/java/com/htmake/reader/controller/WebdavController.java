package com.htmake.reader.controller;

import com.htmake.reader.entity.ReturnData;
import com.htmake.reader.entity.WebdavConfig;
import com.htmake.reader.service.WebdavService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * WebDAV控制器
 */
@Slf4j
@RestController
@RequestMapping("/reader3")
public class WebdavController {

    @Autowired
    private WebdavService webdavService;

    /**
     * 获取WebDAV配置
     */
    @GetMapping("/getWebdavConfig")
    public ReturnData getWebdavConfig(@RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            WebdavConfig config = webdavService.getConfig(username);
            // 不返回密码
            config.setPassword("");
            return ReturnData.success(config);
        } catch (Exception e) {
            log.error("获取WebDAV配置失败", e);
            return ReturnData.error("获取配置失败: " + e.getMessage());
        }
    }

    /**
     * 保存WebDAV配置
     */
    @PostMapping("/saveWebdavConfig")
    public ReturnData saveWebdavConfig(@RequestBody WebdavConfig config,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            boolean success = webdavService.saveConfig(config, username);
            if (success) {
                return ReturnData.success("保存成功");
            } else {
                return ReturnData.error("保存失败");
            }
        } catch (Exception e) {
            log.error("保存WebDAV配置失败", e);
            return ReturnData.error("保存失败: " + e.getMessage());
        }
    }

    /**
     * 备份到WebDAV
     */
    @PostMapping("/webdavBackup")
    public ReturnData backup(@RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            boolean success = webdavService.backup(username);
            if (success) {
                return ReturnData.success("备份成功");
            } else {
                return ReturnData.error("备份失败，请检查WebDAV配置");
            }
        } catch (Exception e) {
            log.error("WebDAV备份失败", e);
            return ReturnData.error("备份失败: " + e.getMessage());
        }
    }

    /**
     * 从WebDAV恢复
     */
    @PostMapping("/webdavRestore")
    public ReturnData restore(@RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            boolean success = webdavService.restore(username);
            if (success) {
                return ReturnData.success("恢复成功");
            } else {
                return ReturnData.error("恢复失败，请检查WebDAV配置");
            }
        } catch (Exception e) {
            log.error("WebDAV恢复失败", e);
            return ReturnData.error("恢复失败: " + e.getMessage());
        }
    }
}
