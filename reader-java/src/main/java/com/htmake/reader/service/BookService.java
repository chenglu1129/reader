package com.htmake.reader.service;

import com.google.gson.Gson;
import com.htmake.reader.config.ReaderConfig;
import com.htmake.reader.entity.Book;
import com.htmake.reader.entity.BookChapter;
import com.htmake.reader.utils.MD5Utils;
import com.htmake.reader.utils.StorageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 书籍服务
 */
@Slf4j
@Service
public class BookService {

    private static final Gson GSON = new Gson();

    @Autowired
    private StorageHelper storageHelper;

    @Autowired
    private ReaderConfig readerConfig;

    /**
     * 获取书架列表
     */
    public List<Book> getShelfBookList(String username) {
        List<Book> bookList = new ArrayList<>();
        String shelfPath = getShelfPath(username);
        File shelfDir = new File(shelfPath);

        if (!shelfDir.exists()) {
            return bookList;
        }

        File[] bookDirs = shelfDir.listFiles(File::isDirectory);
        if (bookDirs != null) {
            for (File bookDir : bookDirs) {
                File bookFile = new File(bookDir, "book.json");
                if (bookFile.exists()) {
                    String json = storageHelper.readFile(bookFile.getAbsolutePath());
                    if (json != null) {
                        try {
                            Book book = GSON.fromJson(json, Book.class);
                            bookList.add(book);
                        } catch (Exception e) {
                            log.error("解析书籍信息失败: {}", bookFile.getAbsolutePath(), e);
                        }
                    }
                }
            }
        }

        return bookList;
    }

    /**
     * 根据URL获取书架上的书籍
     */
    public Book getShelfBookByURL(String bookUrl, String username) {
        if (bookUrl == null || bookUrl.isEmpty()) {
            return null;
        }

        String bookDir = getBookDir(bookUrl, username);
        File bookFile = new File(bookDir, "book.json");

        if (!bookFile.exists()) {
            return null;
        }

        String json = storageHelper.readFile(bookFile.getAbsolutePath());
        if (json == null) {
            return null;
        }

        try {
            return GSON.fromJson(json, Book.class);
        } catch (Exception e) {
            log.error("解析书籍信息失败: {}", bookFile.getAbsolutePath(), e);
            return null;
        }
    }

    /**
     * 保存书籍到书架
     */
    public boolean saveBook(Book book, String username) {
        if (book == null || book.getBookUrl() == null || book.getBookUrl().isEmpty()) {
            return false;
        }

        String bookDir = getBookDir(book.getBookUrl(), username);
        File dir = new File(bookDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File bookFile = new File(dir, "book.json");
        String json = GSON.toJson(book);

        return storageHelper.writeFile(bookFile.getAbsolutePath(), json);
    }

    /**
     * 从书架删除书籍
     */
    public boolean deleteBook(String bookUrl, String username) {
        if (bookUrl == null || bookUrl.isEmpty()) {
            return false;
        }

        String bookDir = getBookDir(bookUrl, username);
        File dir = new File(bookDir);

        if (dir.exists()) {
            return deleteDirectory(dir);
        }

        return true;
    }

    /**
     * 保存章节列表
     */
    public boolean saveChapterList(String bookUrl, List<BookChapter> chapterList, String username) {
        if (bookUrl == null || bookUrl.isEmpty() || chapterList == null) {
            return false;
        }

        String bookDir = getBookDir(bookUrl, username);
        File dir = new File(bookDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File chapterFile = new File(dir, "chapters.json");
        String json = GSON.toJson(chapterList);

        return storageHelper.writeFile(chapterFile.getAbsolutePath(), json);
    }

    /**
     * 获取章节列表
     */
    public List<BookChapter> getChapterList(String bookUrl, String username) {
        if (bookUrl == null || bookUrl.isEmpty()) {
            return new ArrayList<>();
        }

        String bookDir = getBookDir(bookUrl, username);
        File chapterFile = new File(bookDir, "chapters.json");

        if (!chapterFile.exists()) {
            return new ArrayList<>();
        }

        String json = storageHelper.readFile(chapterFile.getAbsolutePath());
        if (json == null) {
            return new ArrayList<>();
        }

        try {
            BookChapter[] chapters = GSON.fromJson(json, BookChapter[].class);
            List<BookChapter> chapterList = new ArrayList<>();
            if (chapters != null) {
                for (BookChapter chapter : chapters) {
                    chapterList.add(chapter);
                }
            }
            return chapterList;
        } catch (Exception e) {
            log.error("解析章节列表失败: {}", chapterFile.getAbsolutePath(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 保存阅读进度
     */
    public boolean saveProgress(String bookUrl, int chapterIndex, String username) {
        Book book = getShelfBookByURL(bookUrl, username);
        if (book == null) {
            return false;
        }

        book.setDurChapterIndex(chapterIndex);
        book.setDurChapterTime(System.currentTimeMillis());
        book.setLatestChapterTime(System.currentTimeMillis());

        return saveBook(book, username);
    }

    /**
     * 获取书架路径
     */
    private String getShelfPath(String username) {
        return storageHelper.getUserDataPath(username) + File.separator + "shelf";
    }

    /**
     * 获取书籍目录
     */
    private String getBookDir(String bookUrl, String username) {
        String md5 = MD5Utils.md5Encode16(bookUrl);
        return getShelfPath(username) + File.separator + md5;
    }

    /**
     * 递归删除目录
     */
    private boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        return directory.delete();
    }
}
