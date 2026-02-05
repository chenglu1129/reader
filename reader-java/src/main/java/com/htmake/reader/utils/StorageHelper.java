package com.htmake.reader.utils;

import com.htmake.reader.config.ReaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 存储助手类
 */
@Component
public class StorageHelper {

    @Autowired
    private ReaderConfig readerConfig;

    /**
     * 获取存储根目录
     */
    public String getStoragePath() {
        String storagePath = readerConfig.getStoragePath();
        if (storagePath == null || storagePath.isEmpty()) {
            storagePath = "storage";
        }

        File storageDir = new File(storagePath);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return storageDir.getAbsolutePath();
    }

    /**
     * 获取用户数据目录
     */
    public String getUserDataPath(String username) {
        String userDir = getStoragePath() + File.separator + "data" + File.separator + username;
        File dir = new File(userDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return userDir;
    }

    /**
     * 获取默认用户数据目录
     */
    public String getDefaultUserDataPath() {
        return getUserDataPath("default");
    }

    /**
     * 获取缓存目录
     */
    public String getCachePath() {
        String cachePath = getStoragePath() + File.separator + "cache";
        File dir = new File(cachePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return cachePath;
    }

    /**
     * 获取本地书仓目录
     */
    public String getLocalStorePath() {
        String localStorePath = getStoragePath() + File.separator + "localStore";
        File dir = new File(localStorePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return localStorePath;
    }

    /**
     * 获取资源目录
     */
    public String getAssetsPath(String username) {
        String assetsPath = getStoragePath() + File.separator + "assets" + File.separator + username;
        File dir = new File(assetsPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return assetsPath;
    }

    /**
     * 读取文件内容
     */
    public String readFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写入文件内容
     */
    public boolean writeFile(String filePath, String content) {
        try {
            Path path = Paths.get(filePath);
            // 确保父目录存在
            Files.createDirectories(path.getParent());
            Files.write(path, content.getBytes("UTF-8"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件是否存在
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}
