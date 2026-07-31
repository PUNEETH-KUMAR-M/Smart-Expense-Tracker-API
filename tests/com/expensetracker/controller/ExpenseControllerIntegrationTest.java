package com.expensetracker.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "expense.storage.file=target/test-expenses.json")
@AutoConfigureMockMvc
class ExpenseControllerIntegrationTest {
    private static final Path TEST_STORAGE_FILE = Path.of("target", "test-expenses.json");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void supportsExpenseCrudTotalsAndValidation() throws Exception {
        String requestBody = """
                {
                  "title": "Groceries",
                  "amount": 85.50,
                  "category": "Food",
                  "date": "2026-07-31"
                }
                """;

        String response = mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Groceries"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expenseId = response.replaceAll(".*\\\"id\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Food"));
        mockMvc.perform(get("/expenses/category/Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(85.50));
        mockMvc.perform(get("/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(85.50));
        mockMvc.perform(get("/expenses/total/Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Food"));
        mockMvc.perform(delete("/expenses/{id}", expenseId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/expenses/{id}", expenseId))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/expenses")
                        .contentType("application/json")
                        .content("{\"title\":\"\",\"amount\":0,\"category\":\"\",\"date\":\"2026-07-31\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value("/expenses"));
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/expenses']").exists())
                .andExpect(jsonPath("$.paths['/expenses/total']").exists());
    }

    @AfterAll
    static void cleanUpStorageFile() throws Exception {
        Files.deleteIfExists(TEST_STORAGE_FILE);
    }
}
