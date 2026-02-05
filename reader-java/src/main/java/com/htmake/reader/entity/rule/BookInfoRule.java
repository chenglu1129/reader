package com.htmake.reader.entity.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 书籍信息规则
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookInfoRule {
    private String init;
    private String name;
    private String author;
    private String intro;
    private String kind;
    private String lastChapter;
    private String updateTime;
    private String coverUrl;
    private String tocUrl;
    private String wordCount;
    private String canReName;
}
