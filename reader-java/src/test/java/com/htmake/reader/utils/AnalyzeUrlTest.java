package com.htmake.reader.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AnalyzeUrl工具类测试
 */
class AnalyzeUrlTest {

    @Test
    void testSimpleUrl() {
        AnalyzeUrl analyzeUrl = new AnalyzeUrl("https://example.com/search");
        assertEquals("https://example.com/search", analyzeUrl.getUrl());
        assertEquals("GET", analyzeUrl.getMethod());
    }

    @Test
    void testUrlWithVariables() {
        Map<String, String> variables = new HashMap<>();
        variables.put("key", "测试");
        variables.put("page", "1");

        AnalyzeUrl analyzeUrl = new AnalyzeUrl("https://example.com/search?q={{key}}&page={{page}}", variables);
        String url = analyzeUrl.getUrl();

        assertTrue(url.contains("q="));
        assertTrue(url.contains("page=1"));
        assertEquals("GET", analyzeUrl.getMethod());
    }

    @Test
    void testPostMethod() {
        AnalyzeUrl analyzeUrl = new AnalyzeUrl("https://example.com/api @Method:POST,{\"key\":\"value\"}");
        assertEquals("POST", analyzeUrl.getMethod());
    }
}
