package com.expensetracker.repository;

import com.expensetracker.model.Expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {
    Expense save(Expense expense);

    List<Expense> findAll();

    List<Expense> findByCategory(String category);

    boolean deleteById(UUID id);

    Optional<Expense> findById(UUID id);

    boolean existsById(UUID id);
}
