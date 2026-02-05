package com.htmake.reader.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 替换规则实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplaceRule {

    /**
     * 规则ID
     */
    private Long id;

    /**
     * 规则名称
     */
    private String name = "";

    /**
     * 规则分组
     */
    private String group = "";

    /**
     * 匹配规则（正则表达式）
     */
    private String pattern = "";

    /**
     * 替换内容
     */
    private String replacement = "";

    /**
     * 适用范围
     */
    private String scope = "";

    /**
     * 是否启用
     */
    private Boolean isEnabled = true;

    /**
     * 是否使用正则表达式
     */
    private Boolean isRegex = true;

    /**
     * 排序序号
     */
    private Integer order = 0;
}
