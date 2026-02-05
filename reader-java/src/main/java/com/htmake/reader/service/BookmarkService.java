package com.htmake.reader.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.htmake.reader.entity.Bookmark;
import com.htmake.reader.utils.StorageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 书签服务
 */
@Slf4j
@Service
public class BookmarkService {

    private static final Gson GSON = new Gson();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(System.currentTimeMillis());

    @Autowired
    private StorageHelper storageHelper;

    /**
     * 获取所有书签
     */
    public List<Bookmark> getAllBookmarks(String username) {
        String path = getBookmarksPath(username);
        File file = new File(path);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        String json = storageHelper.readFile(path);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type listType = new TypeToken<List<Bookmark>>() {
            }.getType();
            List<Bookmark> bookmarks = GSON.fromJson(json, listType);
            return bookmarks != null ? bookmarks : new ArrayList<>();
        } catch (Exception e) {
            log.error("解析书签失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据书籍URL获取书签
     */
    public List<Bookmark> getBookmarksByBook(String bookUrl, String username) {
        return getAllBookmarks(username).stream()
                .filter(b -> bookUrl.equals(b.getBookUrl()))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }

    /**
     * 保存书签
     */
    public boolean saveBookmark(Bookmark bookmark, String username) {
        if (bookmark == null) {
            return false;
        }

        if (bookmark.getId() == null || bookmark.getId() == 0) {
            bookmark.setId(ID_GENERATOR.incrementAndGet());
        }
        if (bookmark.getCreateTime() == null) {
            bookmark.setCreateTime(System.currentTimeMillis());
        }

        List<Bookmark> bookmarks = getAllBookmarks(username);

        boolean found = false;
        for (int i = 0; i < bookmarks.size(); i++) {
            if (bookmarks.get(i).getId().equals(bookmark.getId())) {
                bookmarks.set(i, bookmark);
                found = true;
                break;
            }
        }

        if (!found) {
            bookmarks.add(bookmark);
        }

        return saveAllBookmarks(bookmarks, username);
    }

    /**
     * 删除书签
     */
    public boolean deleteBookmark(Long bookmarkId, String username) {
        if (bookmarkId == null) {
            return false;
        }

        List<Bookmark> bookmarks = getAllBookmarks(username);
        bookmarks.removeIf(b -> bookmarkId.equals(b.getId()));

        return saveAllBookmarks(bookmarks, username);
    }

    /**
     * 保存所有书签
     */
    private boolean saveAllBookmarks(List<Bookmark> bookmarks, String username) {
        String path = getBookmarksPath(username);
        String json = GSON.toJson(bookmarks);
        return storageHelper.writeFile(path, json);
    }

    /**
     * 获取书签文件路径
     */
    private String getBookmarksPath(String username) {
        return storageHelper.getUserDataPath(username) + File.separator + "bookmarks.json";
    }
}
