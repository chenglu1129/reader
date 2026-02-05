package com.htmake.reader.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.htmake.reader.entity.rule.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 书源实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "headerMap", "source" }, ignoreUnknown = true)
public class BookSource {

    /** 书源名称 */
    private String bookSourceName = "";

    /** 书源分组 */
    private String bookSourceGroup;

    /** 书源URL */
    private String bookSourceUrl = "";

    /** 书源类型 0-文本 1-音频 */
    private Integer bookSourceType = 0;

    /** 详情页URL正则 */
    private String bookUrlPattern;

    /** 手动排序编号 */
    private Integer customOrder = 0;

    /** 是否启用 */
    private Boolean enabled = true;

    /** 启用发现 */
    private Boolean enabledExplore = true;

    /** 并发率 */
    private String concurrentRate;

    /** 请求头 */
    private String header;

    /** 登录地址 */
    private String loginUrl;

    /** 登录检测JS */
    private String loginCheckJs;

    /** 最后更新时间 */
    private Long lastUpdateTime = 0L;

    /** 权重 */
    private Integer weight = 0;

    /** 发现URL */
    private String exploreUrl;

    /** 发现规则 */
    private Object ruleExplore;

    /** 搜索URL */
    private String searchUrl;

    /** 搜索规则 */
    private Object ruleSearch;

    /** 书籍信息规则 */
    private Object ruleBookInfo;

    /** 目录规则 */
    private Object ruleToc;

    /** 正文规则 */
    private Object ruleContent;

    /** 书源注释 */
    private String bookSourceComment;

    /** 响应时间 */
    private Long respondTime = 180000L;

    public String getTag() {
        return bookSourceName;
    }

    public String getKey() {
        return bookSourceUrl;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BookSource) {
            return ((BookSource) other).bookSourceUrl.equals(bookSourceUrl);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return bookSourceUrl != null ? bookSourceUrl.hashCode() : 0;
    }

    // 手动实现规则获取，处理多种类型 (Object, String, Array)
    public com.htmake.reader.entity.rule.ExploreRule getRuleExplore() {
        return convertRule(ruleExplore, com.htmake.reader.entity.rule.ExploreRule.class);
    }

    public com.htmake.reader.entity.rule.SearchRule getRuleSearch() {
        return convertRule(ruleSearch, com.htmake.reader.entity.rule.SearchRule.class);
    }

    public com.htmake.reader.entity.rule.BookInfoRule getRuleBookInfo() {
        return convertRule(ruleBookInfo, com.htmake.reader.entity.rule.BookInfoRule.class);
    }

    public com.htmake.reader.entity.rule.TocRule getRuleToc() {
        return convertRule(ruleToc, com.htmake.reader.entity.rule.TocRule.class);
    }

    public com.htmake.reader.entity.rule.ContentRule getRuleContent() {
        return convertRule(ruleContent, com.htmake.reader.entity.rule.ContentRule.class);
    }

    private static final com.google.gson.Gson GSON = new com.google.gson.Gson();

    private <T> T convertRule(Object rule, Class<T> clazz) {
        if (rule == null)
            return null;
        try {
            if (clazz.isInstance(rule)) {
                return clazz.cast(rule);
            }
            String json;
            if (rule instanceof String) {
                json = (String) rule;
            } else if (rule instanceof java.util.Collection) {
                // 如果是数组，尝试取第一个元素 (应对前端发送数组的情况)
                java.util.Collection<?> col = (java.util.Collection<?>) rule;
                if (col.isEmpty())
                    return null;
                Object first = col.iterator().next();
                if (clazz.isInstance(first))
                    return clazz.cast(first);
                json = GSON.toJson(first);
            } else {
                json = GSON.toJson(rule);
            }

            if (json == null || json.trim().isEmpty() || "{}".equals(json.trim()) || "[]".equals(json.trim())) {
                return null;
            }
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }
}
