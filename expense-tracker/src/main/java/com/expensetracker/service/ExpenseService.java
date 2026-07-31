package com.expensetracker.service;

import com.expensetracker.dto.CreateExpenseRequest;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense addExpense(CreateExpenseRequest request) {
        Expense expense = new Expense(
                UUID.randomUUID(),
                request.title().trim(),
                request.amount(),
                request.category().trim(),
                request.date());
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenseRepository.findByCategory(category.trim());
    }

    public double getTotalAmount() {
        return calculateTotal(getAllExpenses());
    }

    public double getTotalAmountByCategory(String category) {
        return calculateTotal(getExpensesByCategory(category));
    }

    public void deleteExpense(UUID id) {
        if (!expenseRepository.deleteById(id)) {
            throw new ExpenseNotFoundException(id);
        }
    }

    private double calculateTotal(List<Expense> expenses) {
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
