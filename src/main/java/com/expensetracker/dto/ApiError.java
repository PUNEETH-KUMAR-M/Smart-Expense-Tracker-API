package com.expensetracker.dto;

import java.time.Instant;

/**
 * Standard immutable structure for API error responses.
 */
public record ApiError(Instant timestamp, int status, String error, String message, String path) {
}
