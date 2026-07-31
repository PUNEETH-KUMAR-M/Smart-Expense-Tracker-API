package com.expensetracker.service;

import com.expensetracker.dto.CreateExpenseRequest;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpenseServiceTest {
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(new InMemoryExpenseRepository());
    }

    @Test
    void addsFiltersTotalsAndDeletesExpenses() {
        Expense foodExpense = expenseService.addExpense(
                new CreateExpenseRequest(" Groceries ", 85.50, " Food ", LocalDate.of(2026, 7, 31)));
        expenseService.addExpense(
                new CreateExpenseRequest("Bus ticket", 20.00, "Travel", LocalDate.of(2026, 7, 30)));

        assertEquals(2, expenseService.getAllExpenses().size());
        assertEquals(1, expenseService.getExpensesByCategory("Food").size());
        assertEquals(105.50, expenseService.getTotalAmount(), 0.001);
        assertEquals(85.50, expenseService.getTotalAmountByCategory("Food"), 0.001);

        expenseService.deleteExpense(foodExpense.getId());

        assertEquals(1, expenseService.getAllExpenses().size());
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.deleteExpense(foodExpense.getId()));
    }

    private static final class InMemoryExpenseRepository implements ExpenseRepository {
        private final List<Expense> expenses = new ArrayList<>();

        @Override
        public Expense save(Expense expense) {
            expenses.removeIf(existingExpense -> existingExpense.getId().equals(expense.getId()));
            expenses.add(expense);
            return expense;
        }

        @Override
        public List<Expense> findAll() {
            return List.copyOf(expenses);
        }

        @Override
        public List<Expense> findByCategory(String category) {
            return expenses.stream()
                    .filter(expense -> expense.getCategory().equals(category))
                    .toList();
        }

        @Override
        public boolean deleteById(UUID id) {
            return expenses.removeIf(expense -> expense.getId().equals(id));
        }

        @Override
        public Optional<Expense> findById(UUID id) {
            return expenses.stream().filter(expense -> expense.getId().equals(id)).findFirst();
        }

        @Override
        public boolean existsById(UUID id) {
            return findById(id).isPresent();
        }
    }
}
