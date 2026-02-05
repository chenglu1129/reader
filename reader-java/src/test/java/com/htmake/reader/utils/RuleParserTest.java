package com.htmake.reader.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RuleParser工具类测试
 */
class RuleParserTest {

    private static final String SAMPLE_HTML = """
            <html>
                <head><title>Test Page</title></head>
                <body>
                    <div class="book-list">
                        <div class="book-item">
                            <h2 class="title">书籍标题1</h2>
                            <span class="author">作者1</span>
                            <a href="/book/1">详情</a>
                        </div>
                        <div class="book-item">
                            <h2 class="title">书籍标题2</h2>
                            <span class="author">作者2</span>
                            <a href="/book/2">详情</a>
                        </div>
                    </div>
                </body>
            </html>
            """;

    private static final String SAMPLE_JSON = """
            {
                "books": [
                    {"name": "Book1", "author": "Author1"},
                    {"name": "Book2", "author": "Author2"}
                ],
                "total": 2
            }
            """;

    @Test
    void testCssSelector() {
        String result = RuleParser.parse(SAMPLE_HTML, "title");
        assertEquals("Test Page", result);
    }

    @Test
    void testCssSelectorWithClass() {
        String result = RuleParser.parse(SAMPLE_HTML, ".book-item .title");
        assertEquals("书籍标题1", result);
    }

    @Test
    void testCssSelectorList() {
        List<String> results = RuleParser.parseList(SAMPLE_HTML, ".book-item .title");
        assertEquals(2, results.size());
        assertEquals("书籍标题1", results.get(0));
        assertEquals("书籍标题2", results.get(1));
    }

    @Test
    void testJsonPath() {
        String result = RuleParser.parse(SAMPLE_JSON, "@json:$.total");
        assertEquals("2", result);
    }

    @Test
    void testRegex() {
        String content = "ISBN: 978-0-123-45678-9";
        String result = RuleParser.parse(content, "@regex:ISBN:\\s*([\\d-]+)");
        assertEquals("978-0-123-45678-9", result);
    }
}
