package com.htmake.reader.controller;

import com.htmake.reader.entity.Book;
import com.htmake.reader.entity.BookChapter;
import com.htmake.reader.entity.ReturnData;
import com.htmake.reader.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 书籍管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/reader3")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 获取书架列表
     */
    @GetMapping("/getShelfBooks")
    public ReturnData getShelfBooks(@RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            List<Book> bookList = bookService.getShelfBookList(username);
            return ReturnData.success(bookList);
        } catch (Exception e) {
            log.error("获取书架列表失败", e);
            return ReturnData.error("获取书架列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取书架列表（兼容原项目接口名：getBookshelf）
     * <p>
     * 前端可能以 GET query 或 POST body 传 refresh 参数；当前实现不依赖 refresh，仅为兼容保留。
     */
    @RequestMapping(value = "/getBookshelf", method = { RequestMethod.GET, RequestMethod.POST })
    public ReturnData getBookshelf(@RequestParam(value = "refresh", required = false) Integer refresh,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            // refresh 参数仅用于兼容（不论是否传入，都返回当前书架列表）
            List<Book> bookList = bookService.getShelfBookList(username);
            return ReturnData.success(bookList);
        } catch (Exception e) {
            log.error("获取书架列表失败", e);
            return ReturnData.error("获取书架列表失败: " + e.getMessage());
        }
    }

    /**
     * 保存书籍到书架
     */
    @PostMapping("/saveBook")
    public ReturnData saveBook(@RequestBody Book book,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (book == null || book.getBookUrl() == null || book.getBookUrl().isEmpty()) {
                return ReturnData.error("书籍信息不完整");
            }

            boolean success = bookService.saveBook(book, username);
            if (success) {
                return ReturnData.success(book);
            } else {
                return ReturnData.error("保存书籍失败");
            }
        } catch (Exception e) {
            log.error("保存书籍失败", e);
            return ReturnData.error("保存书籍失败: " + e.getMessage());
        }
    }

    /**
     * 从书架删除书籍
     */
    @PostMapping("/deleteBook")
    public ReturnData deleteBook(@RequestParam("bookUrl") String bookUrl,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (bookUrl == null || bookUrl.isEmpty()) {
                return ReturnData.error("书籍URL不能为空");
            }

            boolean success = bookService.deleteBook(bookUrl, username);
            if (success) {
                return ReturnData.success("删除成功");
            } else {
                return ReturnData.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除书籍失败", e);
            return ReturnData.error("删除书籍失败: " + e.getMessage());
        }
    }

    /**
     * 获取章节列表
     */
    @RequestMapping(value = "/getChapterList", method = { RequestMethod.GET, RequestMethod.POST })
    public ReturnData getChapterList(@RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "bookUrl", required = false) String bookUrl,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            String finalBookUrl = url != null && !url.isEmpty() ? url : bookUrl;
            if ((finalBookUrl == null || finalBookUrl.isEmpty()) && body != null) {
                Object v = body.get("url");
                if (v != null) {
                    finalBookUrl = String.valueOf(v);
                } else if (body.get("bookUrl") != null) {
                    finalBookUrl = String.valueOf(body.get("bookUrl"));
                } else if (body.get("book") instanceof Map<?, ?>) {
                    Object inner = ((Map<?, ?>) body.get("book")).get("bookUrl");
                    if (inner != null) {
                        finalBookUrl = String.valueOf(inner);
                    }
                }
            }

            if (finalBookUrl == null || finalBookUrl.isEmpty()) {
                return ReturnData.error("请输入书籍链接");
            }

            List<BookChapter> chapterList = bookService.getChapterList(finalBookUrl, username);
            return ReturnData.success(chapterList);
        } catch (Exception e) {
            log.error("获取章节列表失败", e);
            return ReturnData.error("获取章节列表失败: " + e.getMessage());
        }
    }

    /**
     * 保存阅读进度
     */
    @PostMapping("/saveProgress")
    public ReturnData saveProgress(@RequestParam("bookUrl") String bookUrl,
            @RequestParam("chapterIndex") int chapterIndex,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (bookUrl == null || bookUrl.isEmpty()) {
                return ReturnData.error("书籍URL不能为空");
            }

            boolean success = bookService.saveProgress(bookUrl, chapterIndex, username);
            if (success) {
                return ReturnData.success("保存成功");
            } else {
                return ReturnData.error("保存失败");
            }
        } catch (Exception e) {
            log.error("保存阅读进度失败", e);
            return ReturnData.error("保存阅读进度失败: " + e.getMessage());
        }
    }

    /**
     * 保存阅读进度（兼容原项目接口名：saveBookProgress）
     * <p>
     * 兼容参数命名：
     * - 书籍链接：url / bookUrl / body.searchBook.bookUrl
     * - 章节索引：index / chapterIndex
     */
    @RequestMapping(value = "/saveBookProgress", method = { RequestMethod.GET, RequestMethod.POST })
    public ReturnData saveBookProgress(@RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "bookUrl", required = false) String bookUrl,
            @RequestParam(value = "index", required = false) Integer index,
            @RequestParam(value = "chapterIndex", required = false) Integer chapterIndex,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            // 优先使用 query 参数（GET/POST 都可能带 query），其次再从 body 里兜底
            String finalBookUrl = url != null && !url.isEmpty() ? url : bookUrl;
            Integer finalIndex = index != null ? index : chapterIndex;

            if ((finalBookUrl == null || finalBookUrl.isEmpty() || finalIndex == null) && body != null) {
                if (finalBookUrl == null || finalBookUrl.isEmpty()) {
                    // 兼容前端 body: { url, index } 以及部分场景 body: { searchBook: { bookUrl } }
                    Object v = body.get("url");
                    if (v != null) {
                        finalBookUrl = String.valueOf(v);
                    } else if (body.get("bookUrl") != null) {
                        finalBookUrl = String.valueOf(body.get("bookUrl"));
                    } else if (body.get("searchBook") instanceof Map<?, ?>) {
                        Object inner = ((Map<?, ?>) body.get("searchBook")).get("bookUrl");
                        if (inner != null) {
                            finalBookUrl = String.valueOf(inner);
                        }
                    }
                }

                if (finalIndex == null && body.get("index") != null) {
                    // 兼容 body.index 为 number 或字符串
                    Object v = body.get("index");
                    if (v instanceof Number) {
                        finalIndex = ((Number) v).intValue();
                    } else {
                        try {
                            finalIndex = Integer.parseInt(String.valueOf(v));
                        } catch (Exception ignore) {
                            finalIndex = null;
                        }
                    }
                }
                if (finalIndex == null && body.get("chapterIndex") != null) {
                    // 兼容 body.chapterIndex 为 number 或字符串
                    Object v = body.get("chapterIndex");
                    if (v instanceof Number) {
                        finalIndex = ((Number) v).intValue();
                    } else {
                        try {
                            finalIndex = Integer.parseInt(String.valueOf(v));
                        } catch (Exception ignore) {
                            finalIndex = null;
                        }
                    }
                }
            }

            if (finalBookUrl == null || finalBookUrl.isEmpty()) {
                return ReturnData.error("请输入书籍链接");
            }
            if (finalIndex == null || finalIndex < 0) {
                return ReturnData.error("请输入章节索引");
            }

            // BookService.saveProgress 仅对“已在书架”的书生效；未加入书架则返回 false
            boolean success = bookService.saveProgress(finalBookUrl, finalIndex, username);
            if (success) {
                return ReturnData.success("");
            }
            return ReturnData.error("书籍未加入书架");
        } catch (Exception e) {
            log.error("保存阅读进度失败", e);
            return ReturnData.error("保存阅读进度失败: " + e.getMessage());
        }
    }

    /**
     * 搜索书籍
     */
    @GetMapping("/searchBook")
    public ReturnData searchBook(@RequestParam("key") String keyword,
            @RequestParam(value = "sourceUrl", required = false) String sourceUrl,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (keyword == null || keyword.isEmpty()) {
                return ReturnData.error("关键词不能为空");
            }

            // 调用搜索服务
            var result = bookService.searchBooks(keyword, sourceUrl, page, username);
            return ReturnData.success(result);
        } catch (Exception e) {
            log.error("搜索书籍失败", e);
            return ReturnData.error("搜索书籍失败: " + e.getMessage());
        }
    }

    /**
     * 发现书籍 (探索)
     */
    @RequestMapping(value = "/exploreBook", method = { RequestMethod.GET, RequestMethod.POST })
    public ReturnData exploreBook(@RequestParam(value = "ruleFindUrl", required = false) String ruleFindUrl,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "bookSourceUrl", required = false) String sourceUrl,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            String finalUrl = ruleFindUrl;
            int finalPage = page;

            if (body != null) {
                if (body.containsKey("ruleFindUrl")) {
                    finalUrl = (String) body.get("ruleFindUrl");
                }
                if (body.containsKey("page")) {
                    finalPage = (Integer) body.get("page");
                }
            }

            if (finalUrl == null || finalUrl.isEmpty()) {
                return ReturnData.error("链接不能为空");
            }

            // 获取书源URL，如果参数中没有，尝试从body获取 (适配不同前端调用方式)
            String finalSourceUrl = sourceUrl;
            if (finalSourceUrl == null && body != null && body.containsKey("bookSourceUrl")) {
                finalSourceUrl = (String) body.get("bookSourceUrl");
            }

            // 如果还是没有sourceUrl，可能在accessToken里或者其他地方，但通常发现请求会带sourceUrl
            // 在Legado中，如果是发现页面，通常会先选择书源

            var result = bookService.exploreBooks(finalUrl, finalPage, finalSourceUrl, username);
            return ReturnData.success(result);
        } catch (Exception e) {
            log.error("发现书籍失败", e);
            return ReturnData.error("发现书籍失败: " + e.getMessage());
        }
    }

    /**
     * 获取书籍信息
     */
    @GetMapping("/getBookInfo")
    public ReturnData getBookInfo(@RequestParam("bookUrl") String bookUrl,
            @RequestParam(value = "sourceUrl", required = false) String sourceUrl,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (bookUrl == null || bookUrl.isEmpty()) {
                return ReturnData.error("书籍URL不能为空");
            }

            Book book = bookService.getBookInfo(bookUrl, sourceUrl, username);
            if (book != null) {
                return ReturnData.success(book);
            } else {
                return ReturnData.error("获取书籍信息失败");
            }
        } catch (Exception e) {
            log.error("获取书籍信息失败", e);
            return ReturnData.error("获取书籍信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取章节内容
     */
    @GetMapping("/getBookContent")
    public ReturnData getBookContent(@RequestParam("bookUrl") String bookUrl,
            @RequestParam("chapterIndex") int chapterIndex,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (bookUrl == null || bookUrl.isEmpty()) {
                return ReturnData.error("书籍URL不能为空");
            }

            String content = bookService.getChapterContent(bookUrl, chapterIndex, username);
            return ReturnData.success(content);
        } catch (Exception e) {
            log.error("获取章节内容失败", e);
            return ReturnData.error("获取章节内容失败: " + e.getMessage());
        }
    }

    /**
     * 刷新章节列表
     */
    @PostMapping("/refreshChapterList")
    public ReturnData refreshChapterList(@RequestParam("bookUrl") String bookUrl,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (bookUrl == null || bookUrl.isEmpty()) {
                return ReturnData.error("书籍URL不能为空");
            }

            List<BookChapter> chapters = bookService.refreshChapterList(bookUrl, username);
            return ReturnData.success(chapters);
        } catch (Exception e) {
            log.error("刷新章节列表失败", e);
            return ReturnData.error("刷新章节列表失败: " + e.getMessage());
        }
    }
}
