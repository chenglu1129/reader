package com.htmake.reader.entity.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 正文规则
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentRule {
    private String content;
    private String nextContentUrl;
    private String webJs;
    private String sourceRegex;
    private String replaceRegex;
    private String imageStyle;
}
