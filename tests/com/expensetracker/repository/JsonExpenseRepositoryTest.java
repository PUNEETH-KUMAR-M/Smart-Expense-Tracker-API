package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonExpenseRepositoryTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsPersistsAndReloadsExpenses() throws Exception {
        Path storageFile = temporaryDirectory.resolve("expenses.json");
        Expense expense = new Expense(
                UUID.randomUUID(), "Internet", 49.99, "Utilities", LocalDate.of(2026, 7, 31));

        JsonExpenseRepository writer = new JsonExpenseRepository(
                new ObjectMapper().findAndRegisterModules(), storageFile.toString());
        writer.loadExpenses();
        writer.save(expense);

        JsonExpenseRepository reader = new JsonExpenseRepository(
                new ObjectMapper().findAndRegisterModules(), storageFile.toString());
        reader.loadExpenses();

        assertTrue(Files.exists(storageFile));
        assertEquals(List.of(expense), reader.findAll());
    }
}
