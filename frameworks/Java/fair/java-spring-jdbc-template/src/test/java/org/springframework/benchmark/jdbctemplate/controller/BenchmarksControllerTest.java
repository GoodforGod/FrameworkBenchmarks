package org.springframework.benchmark.jdbctemplate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BenchmarksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPlaintext() throws Exception {
        mockMvc.perform(get("/plaintext"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World!"))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN));
    }

    @Test
    void testJson() throws Exception {
        mockMvc.perform(get("/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Hello, World!"));
    }

    @Test
    void testDb() throws Exception {
        mockMvc.perform(get("/db"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.randomNumber").exists());
    }

    @Test
    void testQueries() throws Exception {
        mockMvc.perform(get("/queries").param("queries", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void testUpdates() throws Exception {
        mockMvc.perform(get("/updates").param("queries", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void testFortunes() throws Exception {
        mockMvc.perform(get("/fortunes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8"))
                .andExpect(content().string(org.mockito.ArgumentMatchers.contains("<table>")))
                .andExpect(content().string(org.mockito.ArgumentMatchers.contains("Additional fortune added at request time.")));
    }
}
