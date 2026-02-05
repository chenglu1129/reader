package com.htmake.reader.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htmake.reader.entity.BookSource;
import com.htmake.reader.utils.StorageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 书源服务
 */
@Slf4j
@Service
public class BookSourceService {

    private static final Gson GSON = new Gson();

    @Autowired
    private StorageHelper storageHelper;

    /**
     * 获取所有书源
     */
    public List<BookSource> getAllBookSources(String username) {
        String bookSourcePath = getBookSourcePath(username);
        File bookSourceFile = new File(bookSourcePath);

        if (!bookSourceFile.exists()) {
            return new ArrayList<>();
        }

        String json = storageHelper.readFile(bookSourcePath);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type listType = new TypeToken<List<BookSource>>() {
            }.getType();
            List<BookSource> sources = GSON.fromJson(json, listType);
            return sources != null ? sources : new ArrayList<>();
        } catch (Exception e) {
            log.error("解析书源列表失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据URL获取书源
     */
    public BookSource getBookSourceByUrl(String sourceUrl, String username) {
        if (sourceUrl == null || sourceUrl.isEmpty()) {
            return null;
        }

        List<BookSource> sources = getAllBookSources(username);
        return sources.stream()
                .filter(source -> sourceUrl.equals(source.getBookSourceUrl()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取启用的书源列表
     */
    public List<BookSource> getEnabledBookSources(String username) {
        return getAllBookSources(username).stream()
                .filter(source -> source.getEnabled() != null && source.getEnabled())
                .collect(Collectors.toList());
    }

    /**
     * 根据分组获取书源
     */
    public List<BookSource> getBookSourcesByGroup(String group, String username) {
        if (group == null || group.isEmpty()) {
            return getEnabledBookSources(username);
        }

        return getEnabledBookSources(username).stream()
                .filter(source -> {
                    String sourceGroup = source.getBookSourceGroup();
                    if (sourceGroup == null) {
                        return false;
                    }
                    // 支持多个分组，用逗号分隔
                    String[] groups = sourceGroup.split(",");
                    for (String g : groups) {
                        if (g.trim().equals(group)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * 保存书源
     */
    public boolean saveBookSource(BookSource bookSource, String username) {
        if (bookSource == null || bookSource.getBookSourceUrl() == null || bookSource.getBookSourceUrl().isEmpty()) {
            return false;
        }

        List<BookSource> sources = getAllBookSources(username);

        // 查找是否已存在
        boolean found = false;
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).getBookSourceUrl().equals(bookSource.getBookSourceUrl())) {
                sources.set(i, bookSource);
                found = true;
                break;
            }
        }

        if (!found) {
            sources.add(bookSource);
        }

        return saveAllBookSources(sources, username);
    }

    /**
     * 批量保存书源
     */
    public boolean saveBookSources(List<BookSource> bookSources, String username) {
        if (bookSources == null || bookSources.isEmpty()) {
            return false;
        }

        List<BookSource> existingSources = getAllBookSources(username);

        for (BookSource newSource : bookSources) {
            boolean found = false;
            for (int i = 0; i < existingSources.size(); i++) {
                if (existingSources.get(i).getBookSourceUrl().equals(newSource.getBookSourceUrl())) {
                    existingSources.set(i, newSource);
                    found = true;
                    break;
                }
            }
            if (!found) {
                existingSources.add(newSource);
            }
        }

        return saveAllBookSources(existingSources, username);
    }

    /**
     * 删除书源
     */
    public boolean deleteBookSource(String sourceUrl, String username) {
        if (sourceUrl == null || sourceUrl.isEmpty()) {
            return false;
        }

        List<BookSource> sources = getAllBookSources(username);
        sources.removeIf(source -> sourceUrl.equals(source.getBookSourceUrl()));

        return saveAllBookSources(sources, username);
    }

    /**
     * 批量删除书源
     */
    public boolean deleteBookSources(List<String> sourceUrls, String username) {
        if (sourceUrls == null || sourceUrls.isEmpty()) {
            return false;
        }

        List<BookSource> sources = getAllBookSources(username);
        sources.removeIf(source -> sourceUrls.contains(source.getBookSourceUrl()));

        return saveAllBookSources(sources, username);
    }

    /**
     * 保存所有书源到文件
     */
    private boolean saveAllBookSources(List<BookSource> sources, String username) {
        String bookSourcePath = getBookSourcePath(username);
        String json = GSON.toJson(sources);
        return storageHelper.writeFile(bookSourcePath, json);
    }

    /**
     * 获取书源文件路径
     */
    private String getBookSourcePath(String username) {
        return storageHelper.getUserDataPath(username) + File.separator + "bookSource.json";
    }
}
