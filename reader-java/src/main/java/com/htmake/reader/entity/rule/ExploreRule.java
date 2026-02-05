package com.htmake.reader.entity.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发现规则
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExploreRule {
    private String bookList;
    private String name;
    private String author;
    private String intro;
    private String kind;
    private String lastChapter;
    private String updateTime;
    private String bookUrl;
    private String coverUrl;
    private String wordCount;
}
