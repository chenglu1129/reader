package com.htmake.reader.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 书签实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bookmark {

    /**
     * 书签ID
     */
    private Long id;

    /**
     * 书籍URL
     */
    private String bookUrl = "";

    /**
     * 书籍名称
     */
    private String bookName = "";

    /**
     * 章节索引
     */
    private Integer chapterIndex = 0;

    /**
     * 章节名称
     */
    private String chapterName = "";

    /**
     * 章节位置
     */
    private Integer chapterPos = 0;

    /**
     * 书签内容
     */
    private String content = "";

    /**
     * 创建时间
     */
    private Long createTime = System.currentTimeMillis();
}
