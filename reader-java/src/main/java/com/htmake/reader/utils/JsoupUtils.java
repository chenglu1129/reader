package com.htmake.reader.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Jsoup规则解析工具类
 */
public class JsoupUtils {

    /**
     * 解析HTML文档
     */
    public static Document parse(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }
        return Jsoup.parse(html);
    }

    /**
     * 解析HTML文档（指定baseUri）
     */
    public static Document parse(String html, String baseUri) {
        if (html == null || html.isEmpty()) {
            return null;
        }
        return Jsoup.parse(html, baseUri);
    }

    /**
     * 根据CSS选择器获取元素
     */
    public static Element selectFirst(Document doc, String cssQuery) {
        if (doc == null || cssQuery == null || cssQuery.isEmpty()) {
            return null;
        }
        return doc.selectFirst(cssQuery);
    }

    /**
     * 根据CSS选择器获取元素列表
     */
    public static Elements select(Document doc, String cssQuery) {
        if (doc == null || cssQuery == null || cssQuery.isEmpty()) {
            return new Elements();
        }
        return doc.select(cssQuery);
    }

    /**
     * 获取元素文本
     */
    public static String getText(Element element) {
        if (element == null) {
            return "";
        }
        return element.text();
    }

    /**
     * 获取元素HTML
     */
    public static String getHtml(Element element) {
        if (element == null) {
            return "";
        }
        return element.html();
    }

    /**
     * 获取元素属性
     */
    public static String getAttr(Element element, String attrName) {
        if (element == null || attrName == null || attrName.isEmpty()) {
            return "";
        }
        return element.attr(attrName);
    }

    /**
     * 根据规则获取文本
     */
    public static String getTextByRule(String html, String rule) {
        if (html == null || html.isEmpty() || rule == null || rule.isEmpty()) {
            return "";
        }

        Document doc = parse(html);
        Element element = selectFirst(doc, rule);
        return getText(element);
    }

    /**
     * 根据规则获取文本列表
     */
    public static List<String> getTextListByRule(String html, String rule) {
        List<String> result = new ArrayList<>();

        if (html == null || html.isEmpty() || rule == null || rule.isEmpty()) {
            return result;
        }

        Document doc = parse(html);
        Elements elements = select(doc, rule);

        for (Element element : elements) {
            result.add(getText(element));
        }

        return result;
    }

    /**
     * 根据规则获取属性
     */
    public static String getAttrByRule(String html, String rule, String attrName) {
        if (html == null || html.isEmpty() || rule == null || rule.isEmpty()) {
            return "";
        }

        Document doc = parse(html);
        Element element = selectFirst(doc, rule);
        return getAttr(element, attrName);
    }

    /**
     * 根据规则获取属性列表
     */
    public static List<String> getAttrListByRule(String html, String rule, String attrName) {
        List<String> result = new ArrayList<>();

        if (html == null || html.isEmpty() || rule == null || rule.isEmpty()) {
            return result;
        }

        Document doc = parse(html);
        Elements elements = select(doc, rule);

        for (Element element : elements) {
            result.add(getAttr(element, attrName));
        }

        return result;
    }

    /**
     * 清理HTML标签
     */
    public static String cleanHtml(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }

        Document doc = parse(html);
        return doc.text();
    }
}
