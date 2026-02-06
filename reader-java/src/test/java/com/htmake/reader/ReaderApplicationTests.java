package com.htmake.reader;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
