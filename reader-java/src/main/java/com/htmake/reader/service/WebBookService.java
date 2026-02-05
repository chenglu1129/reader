package com.htmake.reader.service;

import com.google.gson.Gson;
import com.htmake.reader.entity.Book;
import com.htmake.reader.entity.BookSource;
import com.htmake.reader.entity.SearchBook;
import com.htmake.reader.utils.HttpUtils;
import com.htmake.reader.utils.JsoupUtils;
import com.htmake.reader.utils.JsonPathUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络书籍服务
 * 用于从书源获取书籍信息
 */
@Slf4j
@Service
public class WebBookService {

    private static final Gson GSON = new Gson();

    /**
     * 搜索书籍
     */
    public List<SearchBook> searchBook(BookSource bookSource, String keyword, int page) throws IOException {
        List<SearchBook> searchBooks = new ArrayList<>();

        if (bookSource == null || bookSource.getSearchUrl() == null || bookSource.getSearchUrl().isEmpty()) {
            throw new IllegalArgumentException("书源或搜索URL为空");
        }

        // 替换搜索URL中的变量
        String searchUrl = bookSource.getSearchUrl()
                .replace("{{key}}", keyword)
                .replace("{{page}}", String.valueOf(page));

        log.info("搜索书籍: {} - {}", bookSource.getBookSourceName(), searchUrl);

        // 发送HTTP请求
        String html = HttpUtils.get(searchUrl);

        // 解析搜索结果
        if (bookSource.getRuleSearch() != null && !bookSource.getRuleSearch().isEmpty()) {
            searchBooks = parseSearchResult(html, bookSource);
        }

        return searchBooks;
    }

    /**
     * 解析搜索结果
     */
    private List<SearchBook> parseSearchResult(String html, BookSource bookSource) {
        List<SearchBook> searchBooks = new ArrayList<>();

        try {
            // 简单的Jsoup规则解析示例
            Document doc = JsoupUtils.parse(html);

            // 这里需要根据书源的ruleSearch规则来解析
            // 示例：假设ruleSearch包含bookList选择器
            String ruleSearch = bookSource.getRuleSearch();

            // 简化处理：假设规则是JSON格式
            // 实际应该解析复杂的规则字符串
            if (ruleSearch.contains("bookList")) {
                // 这里应该有更复杂的规则解析逻辑
                // 暂时返回空列表
                log.warn("规则解析功能待完善");
            }

        } catch (Exception e) {
            log.error("解析搜索结果失败", e);
        }

        return searchBooks;
    }

    /**
     * 获取书籍信息
     */
    public Book getBookInfo(BookSource bookSource, String bookUrl) throws IOException {
        if (bookSource == null || bookUrl == null || bookUrl.isEmpty()) {
            throw new IllegalArgumentException("书源或书籍URL为空");
        }

        log.info("获取书籍信息: {} - {}", bookSource.getBookSourceName(), bookUrl);

        // 发送HTTP请求
        String html = HttpUtils.get(bookUrl);

        // 解析书籍信息
        Book book = new Book();
        book.setBookUrl(bookUrl);
        book.setOrigin(bookSource.getBookSourceUrl());
        book.setOriginName(bookSource.getBookSourceName());
        book.setType(bookSource.getBookSourceType());

        // 这里需要根据书源的ruleBookInfo规则来解析
        // 暂时返回基本信息
        log.warn("书籍信息解析功能待完善");

        return book;
    }

    /**
     * 简单的规则解析示例
     * 实际应该实现完整的规则引擎
     */
    private String parseRule(String content, String rule, boolean isJson) {
        if (rule == null || rule.isEmpty()) {
            return "";
        }

        try {
            if (isJson) {
                // JSON解析
                return JsonPathUtils.getString(content, rule);
            } else {
                // HTML解析
                return JsoupUtils.getTextByRule(content, rule);
            }
        } catch (Exception e) {
            log.error("规则解析失败: {}", rule, e);
            return "";
        }
    }
}
