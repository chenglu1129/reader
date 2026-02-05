package com.htmake.reader.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 书籍章节实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookChapter {

    /** 章节URL */
    private String url = "";

    /** 章节标题 */
    private String title = "";

    /** 书籍URL */
    private String bookUrl = "";

    /** 章节索引 */
    private Integer index = 0;

    /** 资源URL */
    private String resourceUrl;

    /** 标签 */
    private String tag;

    /** 开始位置 */
    private Long start;

    /** 结束位置 */
    private Long end;

    /** 变量 */
    private String variable;

    /** 是否为VIP章节 */
    private Boolean isVip;

    /** 是否付费 */
    private Boolean isPay;

    /** 章节时间 */
    private Long time;

    @Override
    public boolean equals(Object other) {
        if (other instanceof BookChapter) {
            return ((BookChapter) other).url.equals(url);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
