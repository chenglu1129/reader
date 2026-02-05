package com.htmake.reader.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.htmake.reader.entity.Book;
import com.htmake.reader.entity.BookChapter;
import com.htmake.reader.entity.BookSource;
import com.htmake.reader.entity.SearchBook;
import com.htmake.reader.utils.AnalyzeUrl;
import com.htmake.reader.utils.HttpUtils;
import com.htmake.reader.utils.RuleParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络书籍服务
 * 用于从书源获取书籍信息
 */
@Slf4j
@Service
public class WebBookService {

    private static final Gson GSON = new Gson();

    /**
     * 发现书籍
     */
    public List<SearchBook> exploreBook(BookSource bookSource, String url, int page) throws IOException {
        List<SearchBook> searchBooks = new ArrayList<>();

        if (bookSource == null || url == null || url.isEmpty()) {
            throw new IllegalArgumentException("书源或发现URL为空");
        }

        // 构建变量映射
        Map<String, String> variables = new HashMap<>();
        variables.put("page", String.valueOf(page));

        // 解析URL
        AnalyzeUrl analyzeUrl = new AnalyzeUrl(url, variables);
        String exploreUrl = HttpUtils.normalizeUrl(bookSource.getBookSourceUrl(), analyzeUrl.getUrl());

        log.info("发现书籍: {} - {}", bookSource.getBookSourceName(), exploreUrl);

        // 发送HTTP请求
        String response;
        if ("POST".equals(analyzeUrl.getMethod())) {
            response = HttpUtils.postJson(exploreUrl, analyzeUrl.getBody(), analyzeUrl.getHeaders());
        } else {
            response = HttpUtils.get(exploreUrl, analyzeUrl.getHeaders());
        }

        // 解析发现结果
        searchBooks = parseSearchResult(response, bookSource);

        log.info("发现完成: {} 结果数={}", bookSource.getBookSourceName(), searchBooks.size());
        return searchBooks;
    }

    /**
     * 搜索书籍
     */
    public List<SearchBook> searchBook(BookSource bookSource, String keyword, int page) throws IOException {
        List<SearchBook> searchBooks = new ArrayList<>();

        if (bookSource == null || bookSource.getSearchUrl() == null || bookSource.getSearchUrl().isEmpty()) {
            throw new IllegalArgumentException("书源或搜索URL为空");
        }

        // 构建变量映射
        Map<String, String> variables = new HashMap<>();
        variables.put("key", keyword);
        variables.put("page", String.valueOf(page));
        variables.put("searchKey", keyword);

        // 解析URL
        AnalyzeUrl analyzeUrl = new AnalyzeUrl(bookSource.getSearchUrl(), variables);
        String searchUrl = HttpUtils.normalizeUrl(bookSource.getBookSourceUrl(), analyzeUrl.getUrl());

        log.info("搜索书籍: {} - {}", bookSource.getBookSourceName(), searchUrl);

        // 发送HTTP请求
        String response;
        if ("POST".equals(analyzeUrl.getMethod())) {
            response = HttpUtils.postJson(searchUrl, analyzeUrl.getBody(), analyzeUrl.getHeaders());
        } else {
            response = HttpUtils.get(searchUrl, analyzeUrl.getHeaders());
        }

        // 解析搜索结果
        searchBooks = parseSearchResult(response, bookSource);

        log.info("搜索完成: {} 结果数={}", bookSource.getBookSourceName(), searchBooks.size());
        return searchBooks;
    }

    /**
     * 解析搜索结果
     */
    private List<SearchBook> parseSearchResult(String content, BookSource bookSource) {
        List<SearchBook> searchBooks = new ArrayList<>();

        try {
            var rules = bookSource.getRuleSearch();
            if (rules == null || rules.getBookList() == null || rules.getBookList().isEmpty()) {
                log.warn("书源搜索规则或书籍列表规则为空");
                return searchBooks;
            }

            // 判断响应类型
            boolean isJson = content.trim().startsWith("{") || content.trim().startsWith("[");

            if (isJson) {
                // JSON响应解析
                searchBooks = parseJsonSearchResult(content, rules, bookSource);
            } else {
                // HTML响应解析
                searchBooks = parseHtmlSearchResult(content, rules, bookSource);
            }

        } catch (Exception e) {
            log.error("解析搜索结果失败", e);
        }

        return searchBooks;
    }

    /**
     * 解析HTML搜索结果
     */
    private List<SearchBook> parseHtmlSearchResult(String html, com.htmake.reader.entity.rule.SearchRule rules,
            BookSource bookSource) {
        List<SearchBook> searchBooks = new ArrayList<>();

        try {
            Document doc = Jsoup.parse(html);
            String bookListRule = rules.getBookList();

            Elements bookElements = doc.select(bookListRule);

            for (Element bookElement : bookElements) {
                SearchBook searchBook = new SearchBook();
                searchBook.setOrigin(bookSource.getBookSourceUrl());
                searchBook.setOriginName(bookSource.getBookSourceName());
                searchBook.setType(bookSource.getBookSourceType());

                // 解析各字段
                String nameRule = rules.getName();
                if (nameRule != null) {
                    searchBook.setName(parseElementRule(bookElement, nameRule));
                }

                String authorRule = rules.getAuthor();
                if (authorRule != null) {
                    searchBook.setAuthor(parseElementRule(bookElement, authorRule));
                }

                String kindRule = rules.getKind();
                if (kindRule != null) {
                    searchBook.setKind(parseElementRule(bookElement, kindRule));
                }

                String introRule = rules.getIntro();
                if (introRule != null) {
                    searchBook.setIntro(parseElementRule(bookElement, introRule));
                }

                String bookUrlRule = rules.getBookUrl();
                if (bookUrlRule != null) {
                    String bookUrl = parseElementRule(bookElement, bookUrlRule);
                    // 处理相对URL
                    if (bookUrl != null && !bookUrl.startsWith("http")) {
                        bookUrl = bookSource.getBookSourceUrl() + bookUrl;
                    }
                    searchBook.setBookUrl(bookUrl);
                }

                String coverUrlRule = rules.getCoverUrl();
                if (coverUrlRule != null) {
                    searchBook.setCoverUrl(parseElementRule(bookElement, coverUrlRule));
                }

                // 只添加有名称的搜索结果
                if (searchBook.getName() != null && !searchBook.getName().isEmpty()) {
                    searchBooks.add(searchBook);
                }
            }
        } catch (Exception e) {
            log.error("解析HTML搜索结果失败", e);
        }

        return searchBooks;
    }

    /**
     * 解析JSON搜索结果
     */
    private List<SearchBook> parseJsonSearchResult(String json, com.htmake.reader.entity.rule.SearchRule rules,
            BookSource bookSource) {
        List<SearchBook> searchBooks = new ArrayList<>();

        try {
            String bookListRule = rules.getBookList();
            List<String> bookListJson = RuleParser.parseList(json, "@json:" + bookListRule);

            // 简化处理：JSON解析目前通过 RuleParser 处理具体字段
            log.info("JSON搜索结果解析: 发现{}条", bookListJson.size());

            for (String itemJson : bookListJson) {
                SearchBook searchBook = new SearchBook();
                searchBook.setOrigin(bookSource.getBookSourceUrl());
                searchBook.setOriginName(bookSource.getBookSourceName());
                searchBook.setType(bookSource.getBookSourceType());

                if (rules.getName() != null)
                    searchBook.setName(RuleParser.parse(itemJson, "@json:" + rules.getName()));
                if (rules.getAuthor() != null)
                    searchBook.setAuthor(RuleParser.parse(itemJson, "@json:" + rules.getAuthor()));
                if (rules.getKind() != null)
                    searchBook.setKind(RuleParser.parse(itemJson, "@json:" + rules.getKind()));
                if (rules.getIntro() != null)
                    searchBook.setIntro(RuleParser.parse(itemJson, "@json:" + rules.getIntro()));
                if (rules.getBookUrl() != null) {
                    String bookUrl = RuleParser.parse(itemJson, "@json:" + rules.getBookUrl());
                    if (bookUrl != null && !bookUrl.startsWith("http")) {
                        bookUrl = bookSource.getBookSourceUrl() + bookUrl;
                    }
                    searchBook.setBookUrl(bookUrl);
                }
                if (rules.getCoverUrl() != null)
                    searchBook.setCoverUrl(RuleParser.parse(itemJson, "@json:" + rules.getCoverUrl()));

                if (searchBook.getName() != null && !searchBook.getName().isEmpty()) {
                    searchBooks.add(searchBook);
                }
            }

        } catch (Exception e) {
            log.error("解析JSON搜索结果失败", e);
        }

        return searchBooks;
    }

    /**
     * 解析元素规则
     */
    private String parseElementRule(Element element, String rule) {
        if (rule == null || rule.isEmpty()) {
            return "";
        }

        try {
            // 检查是否有属性获取
            if (rule.contains("@")) {
                int atIndex = rule.indexOf("@");
                String selector = rule.substring(0, atIndex).trim();
                String attr = rule.substring(atIndex + 1).trim();

                Element target = selector.isEmpty() ? element : element.selectFirst(selector);
                if (target == null) {
                    return "";
                }

                if ("text".equals(attr)) {
                    return target.text();
                } else if ("html".equals(attr)) {
                    return target.html();
                } else {
                    return target.attr(attr);
                }
            } else {
                Element target = element.selectFirst(rule);
                return target != null ? target.text() : "";
            }
        } catch (Exception e) {
            log.debug("解析规则失败: {}", rule);
            return "";
        }
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

        // 解析书籍详情
        var rules = bookSource.getRuleBookInfo();
        if (rules != null) {
            try {
                if (rules.getName() != null) {
                    book.setName(RuleParser.parse(html, rules.getName()));
                }
                if (rules.getAuthor() != null) {
                    book.setAuthor(RuleParser.parse(html, rules.getAuthor()));
                }
                if (rules.getIntro() != null) {
                    book.setIntro(RuleParser.parse(html, rules.getIntro()));
                }
                if (rules.getCoverUrl() != null) {
                    book.setCoverUrl(RuleParser.parse(html, rules.getCoverUrl()));
                }
                if (rules.getTocUrl() != null) {
                    book.setTocUrl(RuleParser.parse(html, rules.getTocUrl()));
                }
            } catch (Exception e) {
                log.error("解析书籍详情失败", e);
            }
        }

        return book;
    }

    /**
     * 获取章节列表
     */
    public List<BookChapter> getChapterList(BookSource bookSource, Book book) throws IOException {
        List<BookChapter> chapters = new ArrayList<>();

        if (bookSource == null || book == null || book.getBookUrl() == null) {
            return chapters;
        }

        String tocUrl = book.getTocUrl();
        if (tocUrl == null || tocUrl.isEmpty()) {
            tocUrl = book.getBookUrl();
        }

        log.info("获取章节列表: {}", tocUrl);

        String html = HttpUtils.get(tocUrl);

        var rules = bookSource.getRuleToc();
        if (rules != null) {
            try {
                String chapterListRule = rules.getChapterList();
                if (chapterListRule != null) {
                    Document doc = Jsoup.parse(html);
                    Elements chapterElements = doc.select(chapterListRule);

                    int index = 0;
                    for (Element chapterElement : chapterElements) {
                        BookChapter chapter = new BookChapter();
                        chapter.setIndex(index);

                        String nameRule = rules.getChapterName();
                        if (nameRule != null) {
                            chapter.setTitle(parseElementRule(chapterElement, nameRule));
                        }

                        String urlRule = rules.getChapterUrl();
                        if (urlRule != null) {
                            String chapterUrl = parseElementRule(chapterElement, urlRule);
                            if (chapterUrl != null && !chapterUrl.startsWith("http")) {
                                chapterUrl = bookSource.getBookSourceUrl() + chapterUrl;
                            }
                            chapter.setUrl(chapterUrl);
                        }

                        if (chapter.getTitle() != null && !chapter.getTitle().isEmpty()) {
                            chapters.add(chapter);
                            index++;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("解析章节列表失败", e);
            }
        }

        log.info("获取章节列表完成: 共{}章", chapters.size());
        return chapters;
    }

    /**
     * 获取章节内容
     */
    public String getChapterContent(BookSource bookSource, BookChapter chapter) throws IOException {
        if (bookSource == null || chapter == null || chapter.getUrl() == null) {
            return "";
        }

        log.info("获取章节内容: {}", chapter.getTitle());

        String html = HttpUtils.get(chapter.getUrl());

        var rules = bookSource.getRuleContent();
        if (rules != null) {
            try {
                String contentRule = rules.getContent();
                if (contentRule != null) {
                    return RuleParser.parse(html, contentRule);
                }
            } catch (Exception e) {
                log.error("解析章节内容失败", e);
            }
        }

        return "";
    }
}
