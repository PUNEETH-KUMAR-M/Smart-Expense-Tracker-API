package com.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Request payload used to create an expense.
 */
public record CreateExpenseRequest(
        @NotBlank(message = "Title is required") String title,
        @Positive(message = "Amount must be greater than zero") double amount,
        @NotBlank(message = "Category is required") String category,
        @NotNull(message = "Date is required") LocalDate date) {
}
