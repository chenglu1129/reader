package com.htmake.reader.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * 搜索书籍实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "variableMap", "infoHtml", "tocHtml", "origins", "kindList" }, ignoreUnknown = true)
public class SearchBook implements Comparable<SearchBook> {

    private static final Gson GSON = new Gson();

    /** 书籍URL */
    private String bookUrl = "";

    /** 书源规则 */
    private String origin = "";

    /** 书源名称 */
    private String originName = "";

    /** 类型 */
    private Integer type = 0;

    /** 书名 */
    private String name = "";

    /** 作者 */
    private String author = "";

    /** 分类 */
    private String kind;

    /** 封面URL */
    private String coverUrl;

    /** 简介 */
    private String intro;

    /** 字数 */
    private String wordCount;

    /** 最新章节标题 */
    private String latestChapterTitle;

    /** 目录页URL */
    private String tocUrl = "";

    /** 时间戳 */
    private Long time = 0L;

    /** 变量 */
    private String variable;

    /** 书源排序 */
    private Integer originOrder = 0;

    // 临时变量
    private transient String infoHtml;
    private transient String tocHtml;
    private transient LinkedHashSet<String> origins;
    private transient Map<String, String> variableMap;

    /**
     * 获取变量Map
     */
    public Map<String, String> getVariableMap() {
        if (variableMap == null) {
            try {
                if (variable != null && !variable.isEmpty()) {
                    variableMap = GSON.fromJson(variable, HashMap.class);
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
     * 添加书源
     */
    public void addOrigin(String origin) {
        if (origins == null) {
            origins = new LinkedHashSet<>();
            origins.add(this.origin);
        }
        origins.add(origin);
    }

    /**
     * 转换为Book
     */
    public Book toBook() {
        Book book = new Book();
        book.setName(name);
        book.setAuthor(author);
        book.setKind(kind);
        book.setBookUrl(bookUrl);
        book.setOrigin(origin);
        book.setOriginName(originName);
        book.setType(type);
        book.setWordCount(wordCount);
        book.setLatestChapterTitle(latestChapterTitle);
        book.setCoverUrl(coverUrl);
        book.setIntro(intro);
        book.setTocUrl(tocUrl);
        book.setVariable(variable);
        book.setInfoHtml(infoHtml);
        book.setTocHtml(tocHtml);
        return book;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SearchBook) {
            return ((SearchBook) other).bookUrl.equals(bookUrl);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return bookUrl.hashCode();
    }

    @Override
    public int compareTo(SearchBook other) {
        return other.originOrder - this.originOrder;
    }
}
