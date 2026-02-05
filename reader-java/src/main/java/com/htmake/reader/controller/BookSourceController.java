package com.htmake.reader.controller;

import com.htmake.reader.entity.BookSource;
import com.htmake.reader.entity.ReturnData;
import com.htmake.reader.service.BookSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书源管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/reader3")
public class BookSourceController {

    @Autowired
    private BookSourceService bookSourceService;

    /**
     * 获取所有书源
     */
    @GetMapping("/getBookSources")
    public ReturnData getBookSources(@RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            List<BookSource> sources = bookSourceService.getAllBookSources(username);
            return ReturnData.success(sources);
        } catch (Exception e) {
            log.error("获取书源列表失败", e);
            return ReturnData.error("获取书源列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取启用的书源
     */
    @GetMapping("/getEnabledBookSources")
    public ReturnData getEnabledBookSources(
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            List<BookSource> sources = bookSourceService.getEnabledBookSources(username);
            return ReturnData.success(sources);
        } catch (Exception e) {
            log.error("获取启用书源失败", e);
            return ReturnData.error("获取启用书源失败: " + e.getMessage());
        }
    }

    /**
     * 保存书源
     */
    @PostMapping("/saveBookSource")
    public ReturnData saveBookSource(@RequestBody BookSource bookSource,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (bookSource == null || bookSource.getBookSourceUrl() == null
                    || bookSource.getBookSourceUrl().isEmpty()) {
                return ReturnData.error("书源信息不完整");
            }

            boolean success = bookSourceService.saveBookSource(bookSource, username);
            if (success) {
                return ReturnData.success(bookSource);
            } else {
                return ReturnData.error("保存书源失败");
            }
        } catch (Exception e) {
            log.error("保存书源失败", e);
            return ReturnData.error("保存书源失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存书源
     */
    @PostMapping("/saveBookSources")
    public ReturnData saveBookSources(@RequestBody List<BookSource> bookSources,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (bookSources == null || bookSources.isEmpty()) {
                return ReturnData.error("书源列表不能为空");
            }

            boolean success = bookSourceService.saveBookSources(bookSources, username);
            if (success) {
                return ReturnData.success("保存成功");
            } else {
                return ReturnData.error("保存书源失败");
            }
        } catch (Exception e) {
            log.error("批量保存书源失败", e);
            return ReturnData.error("批量保存书源失败: " + e.getMessage());
        }
    }

    /**
     * 删除书源
     */
    @PostMapping("/deleteBookSource")
    public ReturnData deleteBookSource(@RequestParam("sourceUrl") String sourceUrl,
            @RequestParam(value = "username", defaultValue = "default") String username) {
        try {
            if (sourceUrl == null || sourceUrl.isEmpty()) {
                return ReturnData.error("书源URL不能为空");
            }

            boolean success = bookSourceService.deleteBookSource(sourceUrl, username);
            if (success) {
                return ReturnData.success("删除成功");
            } else {
                return ReturnData.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除书源失败", e);
            return ReturnData.error("删除书源失败: " + e.getMessage());
        }
    }
}
