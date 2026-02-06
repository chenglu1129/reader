package com.htmake.reader;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Spring Boot应用上下文测试
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReaderApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // 验证Spring上下文能够正常加载
    }

    @Test
    void postGetBookSourceNotFoundShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/getBookSource")
                .param("accessToken", "admin:dummy")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void postGetInvalidBookSourcesShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/getInvalidBookSources")
                .param("accessToken", "admin:dummy")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void postGetChapterListWithoutParamsShouldNotBe405() throws Exception {
        mockMvc.perform(post("/reader3/getChapterList")
                .param("accessToken", "admin:dummy")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void postGetChapterListShouldReadUserChapters() throws Exception {
        String user = "u1";
        String bookUrl = "test-url-chapter";
        String md5 = com.htmake.reader.utils.MD5Utils.md5Encode16(bookUrl);
        Path bookDir = Paths.get("storage", "data", user, "shelf", md5);
        Files.createDirectories(bookDir);
        Files.writeString(bookDir.resolve("book.json"),
                "{\"bookUrl\":\"" + bookUrl + "\",\"origin\":\"local\",\"name\":\"n\",\"author\":\"a\"}");
        Files.writeString(bookDir.resolve("chapters.json"),
                "[{\"url\":\"c1\",\"title\":\"t1\",\"bookUrl\":\"" + bookUrl + "\",\"index\":0}]");

        mockMvc.perform(post("/reader3/getChapterList")
                .contentType(APPLICATION_JSON)
                .content("{\"url\":\"" + bookUrl + "\"}")
                .param("accessToken", user + ":token")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data[0].title").value("t1"));
    }

    @Test
    void postGetBookContentWithUrlAndIndexShouldNotBe400() throws Exception {
        String user = "u1";
        String bookUrl = "test-url";
        String md5 = com.htmake.reader.utils.MD5Utils.md5Encode16(bookUrl);
        Path bookDir = Paths.get("storage", "data", user, "shelf", md5);
        Files.createDirectories(bookDir);
        Files.writeString(bookDir.resolve("content_0.txt"), "hello-content");

        mockMvc.perform(post("/reader3/getBookContent")
                .contentType(APPLICATION_JSON)
                .content("{\"url\":\"" + bookUrl + "\",\"index\":0,\"accessToken\":\"" + user + ":token\"}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data").value("hello-content"));
    }

    @Test
    void getBookshelfShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/getBookshelf")
                .param("refresh", "0")
                .param("accessToken", "admin:dummy")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void postSaveBookProgressShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/saveBookProgress")
                .contentType(APPLICATION_JSON)
                .content("{\"url\":\"http://example.com/book\",\"index\":0}")
                .param("accessToken", "admin:dummy")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void postLogoutShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/logout")
                .param("accessToken", "admin:dummy")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void getUserInfoWithoutParamsShouldNotBe400() throws Exception {
        mockMvc.perform(get("/reader3/getUserInfo")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.secure").exists())
                .andExpect(jsonPath("$.data.secureKey").exists());
    }

    @Test
    void getRssSourcesShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/getRssSources")
                .param("simple", "1")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void getShelfBookWithCacheInfoShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/getShelfBookWithCacheInfo")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void getBookGroupsShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/getBookGroups")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postSaveBookGroupOrderShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/saveBookGroupOrder")
                .contentType(APPLICATION_JSON)
                .content("{\"order\":[{\"groupId\":-1,\"order\":0},{\"groupId\":-2,\"order\":1},{\"groupId\":-3,\"order\":2},{\"groupId\":-4,\"order\":3}]}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postSaveBookGroupShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/saveBookGroup")
                .contentType(APPLICATION_JSON)
                .content("{\"groupName\":\"测试分组\",\"show\":true}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postDeleteBookGroupShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/deleteBookGroup")
                .contentType(APPLICATION_JSON)
                .content("{\"groupId\":1}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postDeleteBookShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/deleteBook")
                .contentType(APPLICATION_JSON)
                .content("{\"bookUrl\":\"http://example.com/book\"}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postGetAvailableBookSourceShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/getAvailableBookSource")
                .contentType(APPLICATION_JSON)
                .content("{\"url\":\"http://example.com/book\",\"refresh\":0}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postSaveBookGroupIdShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/saveBookGroupId")
                .contentType(APPLICATION_JSON)
                .content("{\"bookUrl\":\"http://example.com/book\",\"groupId\":1}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void getCacheBookSSEShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/cacheBookSSE")
                .param("url", "http://example.com/book")
                .param("refresh", "0")
                .param("v", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void getSearchBookMultiSSEShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/searchBookMultiSSE")
                .param("key", "测试")
                .param("v", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void getSearchBookSourceSSEShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/searchBookSourceSSE")
                .param("url", "http://example.com/book")
                .param("bookSourceGroup", "音乐 书源")
                .param("concurrentCount", "24")
                .param("v", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void postSearchBookContentShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/searchBookContent")
                .contentType(APPLICATION_JSON)
                .content("{\"url\":\"test-url\",\"keyword\":\"测试\",\"lastIndex\":-1,\"accessToken\":\"test-user:token\"}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postDeleteBookCacheShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/deleteBookCache")
                .contentType(APPLICATION_JSON)
                .content("{\"bookUrl\":\"http://example.com/book\"}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postDeleteBooksShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/deleteBooks")
                .contentType(APPLICATION_JSON)
                .content("[{\"bookUrl\":\"http://example.com/book\"}]")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postAddBookGroupMultiShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/addBookGroupMulti")
                .contentType(APPLICATION_JSON)
                .content("{\"groupId\":1,\"bookList\":[{\"bookUrl\":\"http://example.com/book\"}]}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postRemoveBookGroupMultiShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/removeBookGroupMulti")
                .contentType(APPLICATION_JSON)
                .content("{\"groupId\":1,\"bookList\":[{\"bookUrl\":\"http://example.com/book\"}]}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postImportBookPreviewShouldNotBe404() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file0", "test.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(multipart("/reader3/importBookPreview")
                .file(file)
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void getLocalStoreFileListShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/getLocalStoreFileList")
                .param("path", "/")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postImportFromLocalPathPreviewShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/importFromLocalPathPreview")
                .contentType(APPLICATION_JSON)
                .content("{\"path\":[\"/\"],\"webdav\":false}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void getTxtTocRulesShouldNotBe404() throws Exception {
        mockMvc.perform(get("/reader3/getTxtTocRules")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].rule").exists());
    }

    @Test
    void postDeleteLocalStoreFileShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/deleteLocalStoreFile")
                .contentType(APPLICATION_JSON)
                .content("{\"path\":\"/__not_exists__\"}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postDeleteLocalStoreFileListShouldNotBe404() throws Exception {
        mockMvc.perform(post("/reader3/deleteLocalStoreFileList")
                .contentType(APPLICATION_JSON)
                .content("{\"path\":[\"/__not_exists__\"]}")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void postUploadFileToLocalStoreShouldNotBe404() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file0", "test.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(multipart("/reader3/uploadFileToLocalStore")
                .file(file)
                .param("path", "/")
                .param("v", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").exists());
    }

    @Test
    void getCoverFromCacheShouldReturnBytes() throws Exception {
        String rel = "e8/48/00/e848003a0308794061811c7817378d4e.jpg";
        Path coverPath = Paths.get("storage", "cache", "cover", rel);
        Files.createDirectories(coverPath.getParent());
        byte[] bytes = "abc".getBytes();
        Files.write(coverPath, bytes);

        mockMvc.perform(get("/cover/" + rel))
                .andExpect(status().isOk())
                .andExpect(header().exists("Cache-Control"))
                .andExpect(content().bytes(bytes));
    }
}
