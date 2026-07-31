package com.expensetracker.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Immutable representation of an expense returned by the API.
 */
public record ExpenseResponse(UUID id, String title, double amount, String category, LocalDate date) {
}
