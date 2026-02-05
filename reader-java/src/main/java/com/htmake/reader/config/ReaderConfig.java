package com.htmake.reader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阅读器应用配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "reader.app")
public class ReaderConfig {

    /** 工作目录 */
    private String workDir = "";

    /** 存储路径 */
    private String storagePath = "storage";

    /** 是否需要登录鉴权，开启后将支持多用户模式 */
    private Boolean secure = false;

    /** 注册邀请码 */
    private String inviteCode = "";

    /** 管理密码 */
    private String secureKey = "";

    /** 是否缓存章节内容 */
    private Boolean cacheChapterContent = false;

    /** 是否打开调试日志 */
    private Boolean debugLog = false;

    /** 自动清理不活跃用户天数 */
    private Integer autoClearInactiveUser = 0;

    /** MongoDB URI */
    private String mongoUri = "";

    /** MongoDB 数据库名称 */
    private String mongoDbName = "reader";

    /** 书架自动更新间隔时间（分钟） */
    private Integer shelfUpdateInterval = 10;

    /** 用户上限 */
    private Integer userLimit = 15;

    /** remote-webview 地址 */
    private String remoteWebviewApi = "";

    /** 新用户是否默认启用webdav */
    private Boolean defaultUserEnableWebdav = true;

    /** 新用户是否默认启用localStore */
    private Boolean defaultUserEnableLocalStore = true;

    /** 新用户是否默认可编辑书源 */
    private Boolean defaultUserEnableBookSource = true;

    /** 新用户是否默认可编辑RSS源 */
    private Boolean defaultUserEnableRssSource = true;

    /** 新用户默认书源上限 */
    private Integer defaultUserBookSourceLimit = 100;

    /** 新用户默认书籍上限 */
    private Integer defaultUserBookLimit = 200;

    /** 是否自动备份用户数据 */
    private Boolean autoBackupUserData = false;

    /** 用户密码最小长度 */
    private Integer minUserPasswordLength = 8;

    /** 远程书源定时更新间隔时间（分钟） */
    private Integer remoteBookSourceUpdateInterval = 720;
}
