package com.htmake.reader.controller;

import com.htmake.reader.entity.Bookmark;
import com.htmake.reader.entity.ReturnData;
import com.htmake.reader.service.BookmarkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书签Controller
 */
@Slf4j
@RestController
@RequestMapping("/reader3")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    /**
     * 获取所有书签
     */
    @GetMapping("/getBookmarks")
    public ReturnData getBookmarks(@RequestParam(value = "username", defaultValue = "default") String username,
            @RequestParam(value = "bookUrl", required = false) String bookUrl) {
        try {
            List<Bookmark> bookmarks;
            if (bookUrl != null && !bookUrl.isEmpty()) {
                bookmarks = bookmarkService.getBookmarksByBook(bookUrl, username);
            } else {
                bookmarks = bookmarkService.getAllBookmarks(username);
            }
            return ReturnData.success(bookmarks);
        } catch (Exception e) {
            log.error("获取书签失败", e);
            return ReturnData.error("获取书签失败: " + e.getMessage());
        }
    }

    /**
     * 保存书签
     */
    @PostMapping("/saveBookmark")
    public ReturnData saveBookmark(@RequestBody Bookmark bookmark,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            boolean success = bookmarkService.saveBookmark(bookmark, username);
            if (success) {
                return ReturnData.success(bookmark);
            } else {
                return ReturnData.error("保存书签失败");
            }
        } catch (Exception e) {
            log.error("保存书签失败", e);
            return ReturnData.error("保存书签失败: " + e.getMessage());
        }
    }

    /**
     * 删除书签
     */
    @PostMapping("/deleteBookmark")
    public ReturnData deleteBookmark(@RequestParam("bookmarkId") Long bookmarkId,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            boolean success = bookmarkService.deleteBookmark(bookmarkId, username);
            if (success) {
                return ReturnData.success("删除成功");
            } else {
                return ReturnData.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除书签失败", e);
            return ReturnData.error("删除书签失败: " + e.getMessage());
        }
    }
}
