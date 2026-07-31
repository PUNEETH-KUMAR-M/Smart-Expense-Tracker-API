package com.expensetracker.dto;

/**
 * Total amount for all expenses or for one category.
 *
 * @param totalAmount calculated amount
 * @param category category represented by the total, or {@code null} for an overall total
 */
public record TotalResponse(double totalAmount, String category) {
}
