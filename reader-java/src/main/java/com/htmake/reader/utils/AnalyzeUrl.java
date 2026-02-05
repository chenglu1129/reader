package com.htmake.reader.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URL分析工具类
 * 用于处理书源中的URL模板
 */
@Slf4j
public class AnalyzeUrl {

    private String url;
    private String method = "GET";
    private String body = "";
    private Map<String, String> headers = new HashMap<>();
    private String charset = "UTF-8";

    private static final Pattern URL_PATTERN = Pattern.compile("\\{\\{(.*?)\\}\\}");
    private static final Pattern HEADER_PATTERN = Pattern.compile("@Header:\\{(.*?)\\}");
    private static final Pattern METHOD_PATTERN = Pattern.compile("@Method:(GET|POST|PUT|DELETE)",
            Pattern.CASE_INSENSITIVE);

    public AnalyzeUrl(String ruleUrl) {
        this(ruleUrl, null);
    }

    public AnalyzeUrl(String ruleUrl, Map<String, String> variables) {
        if (ruleUrl == null || ruleUrl.isEmpty()) {
            this.url = "";
            return;
        }

        // 解析URL
        parseUrl(ruleUrl, variables);
    }

    /**
     * 解析URL规则
     */
    private void parseUrl(String ruleUrl, Map<String, String> variables) {
        String urlStr = ruleUrl;

        // 解析请求方法
        Matcher methodMatcher = METHOD_PATTERN.matcher(urlStr);
        if (methodMatcher.find()) {
            this.method = methodMatcher.group(1).toUpperCase();
            urlStr = urlStr.replace(methodMatcher.group(0), "").trim();
        }

        // 解析请求头
        Matcher headerMatcher = HEADER_PATTERN.matcher(urlStr);
        while (headerMatcher.find()) {
            String headerStr = headerMatcher.group(1);
            parseHeaders(headerStr);
            urlStr = urlStr.replace(headerMatcher.group(0), "").trim();
        }

        // 解析POST数据
        if (urlStr.contains(",")) {
            int commaIndex = urlStr.indexOf(",");
            String possibleBody = urlStr.substring(commaIndex + 1).trim();
            if (possibleBody.startsWith("{") || possibleBody.contains("=")) {
                this.body = possibleBody;
                urlStr = urlStr.substring(0, commaIndex).trim();
                if (!this.method.equals("POST")) {
                    this.method = "POST";
                }
            }
        }

        // 替换变量
        if (variables != null && !variables.isEmpty()) {
            urlStr = replaceVariables(urlStr, variables);
            if (!this.body.isEmpty()) {
                this.body = replaceVariables(this.body, variables);
            }
        }

        this.url = urlStr;
    }

    /**
     * 解析请求头
     */
    private void parseHeaders(String headerStr) {
        if (headerStr == null || headerStr.isEmpty()) {
            return;
        }

        try {
            // 支持JSON格式的请求头
            if (headerStr.startsWith("{") && headerStr.endsWith("}")) {
                // 简单JSON解析
                headerStr = headerStr.substring(1, headerStr.length() - 1);
                String[] pairs = headerStr.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split(":");
                    if (kv.length == 2) {
                        String key = kv[0].trim().replaceAll("\"", "");
                        String value = kv[1].trim().replaceAll("\"", "");
                        headers.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析请求头失败: {}", headerStr, e);
        }
    }

    /**
     * 替换变量
     */
    private String replaceVariables(String str, Map<String, String> variables) {
        Matcher matcher = URL_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = variables.getOrDefault(varName, "");

            // URL编码
            try {
                replacement = URLEncoder.encode(replacement, StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                log.error("URL编码失败: {}", replacement, e);
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
