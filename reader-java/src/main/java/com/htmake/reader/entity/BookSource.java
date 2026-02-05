package com.htmake.reader.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String ruleExplore;

    /** 搜索URL */
    private String searchUrl;

    /** 搜索规则 */
    private String ruleSearch;

    /** 书籍信息规则 */
    private String ruleBookInfo;

    /** 目录规则 */
    private String ruleToc;

    /** 正文规则 */
    private String ruleContent;

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
        return bookSourceUrl.hashCode();
    }
}
