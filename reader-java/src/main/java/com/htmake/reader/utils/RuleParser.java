package com.htmake.reader.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 规则解析器
 * 用于解析书源规则，支持CSS选择器、JsonPath、XPath和正则表达式
 */
@Slf4j
public class RuleParser {

    // 规则类型标识
    private static final String CSS_PREFIX = "@css:";
    private static final String JSON_PREFIX = "@json:";
    private static final String XPATH_PREFIX = "@xpath:";
    private static final String REGEX_PREFIX = "@regex:";

    // 属性获取标识
    private static final Pattern ATTR_PATTERN = Pattern.compile("@attr\\((.*?)\\)");
    private static final Pattern TEXT_PATTERN = Pattern.compile("@text");
    private static final Pattern HTML_PATTERN = Pattern.compile("@html");

    /**
     * 解析规则获取单个结果
     */
    public static String parse(String content, String rule) {
        if (content == null || content.isEmpty() || rule == null || rule.isEmpty()) {
            return "";
        }

        try {
            // 判断规则类型
            if (rule.startsWith(JSON_PREFIX)) {
                return parseJson(content, rule.substring(JSON_PREFIX.length()));
            } else if (rule.startsWith(XPATH_PREFIX)) {
                return parseXpath(content, rule.substring(XPATH_PREFIX.length()));
            } else if (rule.startsWith(REGEX_PREFIX)) {
                return parseRegex(content, rule.substring(REGEX_PREFIX.length()));
            } else if (rule.startsWith(CSS_PREFIX)) {
                return parseCss(content, rule.substring(CSS_PREFIX.length()));
            } else {
                // 默认使用CSS选择器
                return parseCss(content, rule);
            }
        } catch (Exception e) {
            log.error("规则解析失败: rule={}", rule, e);
            return "";
        }
    }

    /**
     * 解析规则获取多个结果
     */
    public static List<String> parseList(String content, String rule) {
        List<String> results = new ArrayList<>();

        if (content == null || content.isEmpty() || rule == null || rule.isEmpty()) {
            return results;
        }

        try {
            // 判断规则类型
            if (rule.startsWith(JSON_PREFIX)) {
                return parseJsonList(content, rule.substring(JSON_PREFIX.length()));
            } else if (rule.startsWith(XPATH_PREFIX)) {
                return parseXpathList(content, rule.substring(XPATH_PREFIX.length()));
            } else if (rule.startsWith(REGEX_PREFIX)) {
                return parseRegexList(content, rule.substring(REGEX_PREFIX.length()));
            } else if (rule.startsWith(CSS_PREFIX)) {
                return parseCssList(content, rule.substring(CSS_PREFIX.length()));
            } else {
                // 默认使用CSS选择器
                return parseCssList(content, rule);
            }
        } catch (Exception e) {
            log.error("规则解析失败: rule={}", rule, e);
            return results;
        }
    }

    /**
     * CSS选择器解析
     */
    private static String parseCss(String html, String rule) {
        Document doc = JsoupUtils.parse(html);
        if (doc == null) {
            return "";
        }

        // 分离选择器和属性获取
        String selector = rule;
        String attrName = null;
        boolean getText = true;
        boolean getHtml = false;

        Matcher attrMatcher = ATTR_PATTERN.matcher(rule);
        if (attrMatcher.find()) {
            attrName = attrMatcher.group(1);
            selector = rule.substring(0, attrMatcher.start()).trim();
            getText = false;
        } else if (TEXT_PATTERN.matcher(rule).find()) {
            selector = rule.replaceAll("@text", "").trim();
            getText = true;
        } else if (HTML_PATTERN.matcher(rule).find()) {
            selector = rule.replaceAll("@html", "").trim();
            getHtml = true;
            getText = false;
        }

        Element element = doc.selectFirst(selector);
        if (element == null) {
            return "";
        }

        if (attrName != null) {
            return element.attr(attrName);
        } else if (getHtml) {
            return element.html();
        } else {
            return element.text();
        }
    }

    /**
     * CSS选择器解析多个结果
     */
    private static List<String> parseCssList(String html, String rule) {
        List<String> results = new ArrayList<>();
        Document doc = JsoupUtils.parse(html);
        if (doc == null) {
            return results;
        }

        // 分离选择器和属性获取
        String selector = rule;
        String attrName = null;
        boolean getText = true;
        boolean getHtml = false;

        Matcher attrMatcher = ATTR_PATTERN.matcher(rule);
        if (attrMatcher.find()) {
            attrName = attrMatcher.group(1);
            selector = rule.substring(0, attrMatcher.start()).trim();
            getText = false;
        } else if (TEXT_PATTERN.matcher(rule).find()) {
            selector = rule.replaceAll("@text", "").trim();
            getText = true;
        } else if (HTML_PATTERN.matcher(rule).find()) {
            selector = rule.replaceAll("@html", "").trim();
            getHtml = true;
            getText = false;
        }

        Elements elements = doc.select(selector);
        for (Element element : elements) {
            if (attrName != null) {
                results.add(element.attr(attrName));
            } else if (getHtml) {
                results.add(element.html());
            } else {
                results.add(element.text());
            }
        }

        return results;
    }

    /**
     * JsonPath解析
     */
    private static String parseJson(String json, String rule) {
        return JsonPathUtils.getString(json, rule);
    }

    /**
     * JsonPath解析多个结果
     */
    private static List<String> parseJsonList(String json, String rule) {
        return JsonPathUtils.getStringList(json, rule);
    }

    /**
     * XPath解析（简化实现，后续可扩展）
     */
    private static String parseXpath(String html, String rule) {
        // TODO: 实现完整的XPath解析
        log.warn("XPath解析待完善: {}", rule);
        return parseCss(html, rule);
    }

    /**
     * XPath解析多个结果
     */
    private static List<String> parseXpathList(String html, String rule) {
        // TODO: 实现完整的XPath解析
        log.warn("XPath解析待完善: {}", rule);
        return parseCssList(html, rule);
    }

    /**
     * 正则表达式解析
     */
    private static String parseRegex(String content, String rule) {
        try {
            Pattern pattern = Pattern.compile(rule);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.groupCount() > 0 ? matcher.group(1) : matcher.group();
            }
        } catch (Exception e) {
            log.error("正则解析失败: {}", rule, e);
        }
        return "";
    }

    /**
     * 正则表达式解析多个结果
     */
    private static List<String> parseRegexList(String content, String rule) {
        List<String> results = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(rule);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                results.add(matcher.groupCount() > 0 ? matcher.group(1) : matcher.group());
            }
        } catch (Exception e) {
            log.error("正则解析失败: {}", rule, e);
        }
        return results;
    }
}
