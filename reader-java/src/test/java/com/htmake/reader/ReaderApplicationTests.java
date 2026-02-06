package com.htmake.reader;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/event-stream"));
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
}
