package com.htmake.reader.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 书籍实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "variableMap", "infoHtml", "tocHtml", "config", "rootDir",
        "readConfig", "localBook", "epub", "epubRootDir", "onLineTxt", "localTxt", "umd",
        "realAuthor", "unreadChapterNum", "folderName", "localFile", "kindList", "_userNameSpace",
        "bookDir", "userNameSpace" }, ignoreUnknown = true)
public class Book {

    private static final Gson GSON = new Gson();
    private static final String BOOK_TYPE_LOCAL = "local";

    /** 详情页Url(本地书源存储完整文件路径) */
    private String bookUrl = "";

    /** 目录页Url */
    private String tocUrl = "";

    /** 书源URL */
    private String origin = BOOK_TYPE_LOCAL;

    /** 书源名称 */
    private String originName = "";

    /** 书籍名称 */
    private String name = "";

    /** 作者名称 */
    private String author = "";

    /** 分类信息(书源获取) */
    private String kind;

    /** 分类信息(用户修改) */
    private String customTag;

    /** 封面Url(书源获取) */
    private String coverUrl;

    /** 封面Url(用户修改) */
    private String customCoverUrl;

    /** 简介内容(书源获取) */
    private String intro;

    /** 简介内容(用户修改) */
    private String customIntro;

    /** 自定义字符集名称 */
    private String charset;

    /** 类型 0-文本 1-音频 */
    private Integer type = 0;

    /** 自定义分组索引号 */
    private Integer group = 0;

    /** 最新章节标题 */
    private String latestChapterTitle;

    /** 最新章节标题更新时间 */
    private Long latestChapterTime = System.currentTimeMillis();

    /** 最近一次更新书籍信息的时间 */
    private Long lastCheckTime = System.currentTimeMillis();

    /** 最近一次发现新章节的数量 */
    private Integer lastCheckCount = 0;

    /** 书籍目录总数 */
    private Integer totalChapterNum = 0;

    /** 当前章节名称 */
    private String durChapterTitle;

    /** 当前章节索引 */
    private Integer durChapterIndex = 0;

    /** 当前阅读的进度 */
    private Integer durChapterPos = 0;

    /** 最近一次阅读书籍的时间 */
    private Long durChapterTime = System.currentTimeMillis();

    /** 字数 */
    private String wordCount;

    /** 刷新书架时更新书籍信息 */
    private Boolean canUpdate = true;

    /** 手动排序 */
    private Integer order = 0;

    /** 书源排序 */
    private Integer originOrder = 0;

    /** 正文使用净化替换规则 */
    private Boolean useReplaceRule = true;

    /** 自定义书籍变量信息 */
    private String variable;

    /** 阅读配置 */
    private ReadConfig readConfig;

    // 临时变量
    private transient String infoHtml;
    private transient String tocHtml;
    private transient Map<String, String> variableMap;
    private transient String rootDir = "";
    private transient String _userNameSpace = "";

    /**
     * 是否为本地书籍
     */
    public boolean isLocalBook() {
        return BOOK_TYPE_LOCAL.equals(origin);
    }

    /**
     * 是否为本地TXT
     */
    public boolean isLocalTxt() {
        return isLocalBook() && originName != null && originName.toLowerCase().endsWith(".txt");
    }

    /**
     * 是否为本地EPUB
     */
    public boolean isLocalEpub() {
        return isLocalBook() && originName != null && originName.toLowerCase().endsWith(".epub");
    }

    /**
     * 是否为EPUB
     */
    public boolean isEpub() {
        return originName != null && originName.toLowerCase().endsWith(".epub");
    }

    /**
     * 是否为在线文本
     */
    public boolean isOnLineTxt() {
        return !isLocalBook() && type != null && type == 0;
    }

    /**
     * 获取显示封面
     */
    public String getDisplayCover() {
        return (customCoverUrl == null || customCoverUrl.isEmpty()) ? coverUrl : customCoverUrl;
    }

    /**
     * 获取显示简介
     */
    public String getDisplayIntro() {
        return (customIntro == null || customIntro.isEmpty()) ? intro : customIntro;
    }

    /**
     * 获取文件字符集
     */
    public Charset fileCharset() {
        try {
            return Charset.forName(charset != null ? charset : "UTF-8");
        } catch (Exception e) {
            return StandardCharsets.UTF_8;
        }
    }

    /**
     * 获取变量Map
     */
    public Map<String, String> getVariableMap() {
        if (variableMap == null) {
            try {
                if (variable != null && !variable.isEmpty()) {
                    variableMap = GSON.fromJson(variable, new TypeToken<HashMap<String, String>>() {
                    }.getType());
                }
            } catch (Exception e) {
                variableMap = new HashMap<>();
            }
            if (variableMap == null) {
                variableMap = new HashMap<>();
            }
        }
        return variableMap;
    }

    /**
     * 设置变量
     */
    public void putVariable(String key, String value) {
        Map<String, String> map = getVariableMap();
        if (value != null) {
            map.put(key, value);
        } else {
            map.remove(key);
        }
        variable = GSON.toJson(map);
    }

    /**
     * 获取文件夹名称
     */
    public String getFolderName() {
        String folderName = name.replaceAll("[\\\\/:*?\"<>|]", "");
        int length = Math.min(9, folderName.length());
        folderName = folderName.substring(0, length);
        return folderName + md5Encode16(bookUrl);
    }

    /**
     * 设置根目录
     */
    public void setRootDir(String root) {
        if (root != null && !root.isEmpty() && !root.endsWith(File.separator)) {
            rootDir = root + File.separator;
        } else {
            rootDir = root != null ? root : "";
        }
    }

    /**
     * 设置用户命名空间
     */
    public void setUserNameSpace(String nameSpace) {
        _userNameSpace = nameSpace != null ? nameSpace : "";
    }

    /**
     * 获取用户命名空间
     */
    public String getUserNameSpace() {
        return _userNameSpace;
    }

    /**
     * 转换为SearchBook
     */
    public SearchBook toSearchBook() {
        SearchBook searchBook = new SearchBook();
        searchBook.setName(name);
        searchBook.setAuthor(author);
        searchBook.setKind(kind);
        searchBook.setBookUrl(bookUrl);
        searchBook.setOrigin(origin);
        searchBook.setOriginName(originName);
        searchBook.setType(type);
        searchBook.setWordCount(wordCount);
        searchBook.setLatestChapterTitle(latestChapterTitle);
        searchBook.setCoverUrl(coverUrl);
        searchBook.setIntro(intro);
        searchBook.setTocUrl(tocUrl);
        searchBook.setVariable(variable);
        searchBook.setInfoHtml(infoHtml);
        searchBook.setTocHtml(tocHtml);
        return searchBook;
    }

    /**
     * MD5 16位编码
     */
    private String md5Encode16(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 4; i < 12; i++) {
                sb.append(String.format("%02x", array[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Book) {
            return ((Book) other).bookUrl.equals(bookUrl);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return bookUrl.hashCode();
    }

    /**
     * 阅读配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadConfig {
        /** 倒序目录 */
        private Boolean reverseToc = false;

        /** 翻页动画 */
        private Integer pageAnim = -1;

        /** 重新分段 */
        private Boolean reSegment = false;

        /** 图片样式 */
        private String imageStyle;

        /** 正文使用净化替换规则 */
        private Boolean useReplaceRule = false;

        /** 去除标签 */
        private Long delTag = 0L;
    }
}
