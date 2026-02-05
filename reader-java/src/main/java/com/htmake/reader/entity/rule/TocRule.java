package com.htmake.reader.entity.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 目录规则
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TocRule {
    private String preUpdateJs;
    private String chapterList;
    private String chapterName;
    private String chapterUrl;
    private String isVolume;
    private String isVip;
    private String updateTime;
    private String nextTocUrl;
}
