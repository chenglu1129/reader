package com.htmake.reader.controller;

import com.google.gson.Gson;
import com.htmake.reader.entity.Book;
import com.htmake.reader.utils.HttpUtils;
import com.htmake.reader.utils.MD5Utils;
import com.htmake.reader.utils.StorageHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
public class CoverController {

    private static final Gson GSON = new Gson();

    @Autowired
    private StorageHelper storageHelper;

    @GetMapping("/reader3/cover")
    public ResponseEntity<Resource> getBookCover(@RequestParam(value = "path", required = false) String coverUrl) {
        try {
            if (coverUrl == null || coverUrl.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String normalizedUrl = normalizeCoverUrl(coverUrl);
            String ext = getFileExt(coverUrl, "png");
            String md5 = MD5Utils.md5Encode(coverUrl);

            Path nestedCache = getNestedCoverCachePath(md5, ext);
            Path flatCache = Paths.get(storageHelper.getCachePath(), md5 + "." + ext);
            Path flatCoverCache = Paths.get(storageHelper.getCachePath(), "cover", md5 + "." + ext);

            if (Files.exists(nestedCache)) {
                return fileResponse(nestedCache);
            }
            if (Files.exists(flatCoverCache)) {
                return fileResponse(flatCoverCache);
            }
            if (Files.exists(flatCache)) {
                return fileResponse(flatCache);
            }

            Files.createDirectories(nestedCache.getParent());

            Request request = new Request.Builder().url(normalizedUrl).get().build();
            try (Response response = HttpUtils.getClient().newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return ResponseEntity.notFound().build();
                }
                ResponseBody body = response.body();
                if (body == null) {
                    return ResponseEntity.notFound().build();
                }
                byte[] bytes = body.bytes();
                if (bytes.length == 0) {
                    return ResponseEntity.notFound().build();
                }
                Files.write(nestedCache, bytes);
            }

            return fileResponse(nestedCache);
        } catch (Exception e) {
            log.warn("获取封面失败", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cover/**")
    public ResponseEntity<Resource> getCoverFile(HttpServletRequest request) {
        try {
            String uri = request.getRequestURI();
            String contextPath = request.getContextPath() == null ? "" : request.getContextPath();
            String path = uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;

            String prefix = "/cover/";
            int idx = path.indexOf(prefix);
            if (idx < 0) {
                return ResponseEntity.notFound().build();
            }
            String rel = path.substring(idx + prefix.length());
            if (rel.isEmpty() || rel.contains("..") || rel.contains(":") || rel.startsWith("/") || rel.startsWith("\\")) {
                return ResponseEntity.notFound().build();
            }

            Path candidate1 = Paths.get(storageHelper.getCachePath(), "cover", rel);
            Path candidate2 = Paths.get(storageHelper.getCachePath(), rel);

            if (Files.exists(candidate1)) {
                return fileResponse(candidate1);
            }
            if (Files.exists(candidate2)) {
                return fileResponse(candidate2);
            }

            String fileName = Paths.get(rel).getFileName().toString();
            int dot = fileName.lastIndexOf('.');
            if (dot <= 0) {
                return ResponseEntity.notFound().build();
            }

            String md5 = fileName.substring(0, dot).toLowerCase(Locale.ROOT);
            String ext = fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
            if (!md5.matches("^[a-f0-9]{32}$")) {
                return ResponseEntity.notFound().build();
            }
            if (!ext.matches("^[a-z0-9]{1,10}$")) {
                return ResponseEntity.notFound().build();
            }

            String coverUrl = findCoverUrlByMd5(md5);
            if (coverUrl == null || coverUrl.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Path target = candidate1;
            Files.createDirectories(target.getParent());

            Request httpRequest = new Request.Builder().url(normalizeCoverUrl(coverUrl)).get().build();
            try (Response response = HttpUtils.getClient().newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    return ResponseEntity.notFound().build();
                }
                ResponseBody body = response.body();
                if (body == null) {
                    return ResponseEntity.notFound().build();
                }
                byte[] bytes = body.bytes();
                if (bytes.length == 0) {
                    return ResponseEntity.notFound().build();
                }
                Files.write(target, bytes);
            }

            return fileResponse(target);
        } catch (Exception e) {
            log.warn("读取封面缓存失败", e);
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<Resource> fileResponse(Path file) {
        Resource resource = new FileSystemResource(file.toFile());
        MediaType contentType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(contentType)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(resource);
    }

    private Path getNestedCoverCachePath(String md5, String ext) {
        String a = md5.substring(0, 2);
        String b = md5.substring(2, 4);
        String c = md5.substring(4, 6);
        return Paths.get(storageHelper.getCachePath(), "cover", a, b, c, md5 + "." + ext);
    }

    private String getFileExt(String url, String defaultExt) {
        if (defaultExt == null || defaultExt.isEmpty()) {
            defaultExt = "png";
        }
        if (url == null || url.isEmpty()) {
            return defaultExt;
        }
        String u = url;
        int q = u.indexOf('?');
        if (q >= 0) {
            u = u.substring(0, q);
        }
        int hash = u.indexOf('#');
        if (hash >= 0) {
            u = u.substring(0, hash);
        }
        int dot = u.lastIndexOf('.');
        if (dot < 0 || dot == u.length() - 1) {
            return defaultExt;
        }
        String ext = u.substring(dot + 1).trim().toLowerCase(Locale.ROOT);
        if (!ext.matches("^[a-z0-9]{1,10}$")) {
            return defaultExt;
        }
        return ext;
    }

    private String normalizeCoverUrl(String coverUrl) {
        if (coverUrl == null) {
            return "";
        }
        if (coverUrl.startsWith("//")) {
            return "http:" + coverUrl;
        }
        return coverUrl;
    }

    private String findCoverUrlByMd5(String md5) {
        try {
            Path storageRoot = Paths.get(storageHelper.getStoragePath());
            Path dataDir = storageRoot.resolve("data");
            if (!Files.exists(dataDir) || !Files.isDirectory(dataDir)) {
                return "";
            }

            List<Path> userDirs = new ArrayList<>();
            try (var stream = Files.list(dataDir)) {
                stream.filter(Files::isDirectory).forEach(userDirs::add);
            }
            if (userDirs.isEmpty()) {
                userDirs.add(dataDir.resolve("default"));
            }

            for (Path userDir : userDirs) {
                Path shelfDir = userDir.resolve("shelf");
                if (!Files.exists(shelfDir) || !Files.isDirectory(shelfDir)) {
                    continue;
                }
                try (var stream = Files.list(shelfDir)) {
                    for (Path bookDir : stream.filter(Files::isDirectory).toList()) {
                        Path bookJson = bookDir.resolve("book.json");
                        if (!Files.exists(bookJson)) {
                            continue;
                        }
                        String json = Files.readString(bookJson);
                        if (json == null || json.isEmpty()) {
                            continue;
                        }
                        Book book;
                        try {
                            book = GSON.fromJson(json, Book.class);
                        } catch (Exception ignore) {
                            continue;
                        }
                        if (book == null) {
                            continue;
                        }
                        String url = book.getDisplayCover();
                        if (url == null || url.isEmpty()) {
                            continue;
                        }
                        if (!(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("//"))) {
                            continue;
                        }
                        String m = MD5Utils.md5Encode(url);
                        if (md5.equalsIgnoreCase(m)) {
                            return url;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }
}

