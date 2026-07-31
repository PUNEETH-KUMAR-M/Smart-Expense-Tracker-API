package com.expensetracker.controller;

import com.expensetracker.dto.CreateExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.TotalResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(@Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse response = toResponse(expenseService.addExpense(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        return ResponseEntity.ok(toResponses(expenseService.getAllExpenses()));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(toResponses(expenseService.getExpensesByCategory(category)));
    }

    @GetMapping("/total")
    public ResponseEntity<TotalResponse> getTotalAmount() {
        return ResponseEntity.ok(new TotalResponse(expenseService.getTotalAmount(), null));
    }

    @GetMapping("/total/{category}")
    public ResponseEntity<TotalResponse> getTotalAmountByCategory(@PathVariable String category) {
        return ResponseEntity.ok(new TotalResponse(expenseService.getTotalAmountByCategory(category), category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    private List<ExpenseResponse> toResponses(List<Expense> expenses) {
        return expenses.stream().map(this::toResponse).toList();
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate());
    }
}
