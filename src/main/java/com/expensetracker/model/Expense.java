package com.expensetracker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Expense {
    @NotNull(message = "Id is required")
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    @Positive(message = "Amount must be greater than zero")
    private double amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    public Expense() {
    }

    public Expense(UUID id, String title, double amount, String category, LocalDate date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Expense other)) {
            return false;
        }
        return Double.compare(amount, other.amount) == 0
                && Objects.equals(id, other.id)
                && Objects.equals(title, other.title)
                && Objects.equals(category, other.category)
                && Objects.equals(date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, amount, category, date);
    }

    @Override
    public String toString() {
        return "Expense{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", amount=" + amount
                + ", category='" + category + '\''
                + ", date=" + date
                + '}';
    }
}
