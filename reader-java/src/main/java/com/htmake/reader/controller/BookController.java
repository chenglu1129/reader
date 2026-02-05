package com.htmake.reader.controller;

import com.htmake.reader.entity.Book;
import com.htmake.reader.entity.BookChapter;
import com.htmake.reader.entity.ReturnData;
import com.htmake.reader.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/getChapterList")
    public ReturnData getChapterList(@RequestParam("bookUrl") String bookUrl,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (bookUrl == null || bookUrl.isEmpty()) {
                return ReturnData.error("书籍URL不能为空");
            }

            List<BookChapter> chapterList = bookService.getChapterList(bookUrl, username);
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
