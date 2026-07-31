package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class JsonExpenseRepository implements ExpenseRepository {
    private static final String STORAGE_FILE_NAME = "expenses.json";
    private static final TypeReference<List<Expense>> EXPENSE_LIST_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storagePath;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private List<Expense> expenses = new ArrayList<>();

    public JsonExpenseRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.storagePath = Path.of(STORAGE_FILE_NAME).toAbsolutePath();
    }

    @PostConstruct
    void loadExpenses() {
        lock.writeLock().lock();
        try {
            initializeStorageFile();
            expenses = readExpenses();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Expense save(Expense expense) {
        lock.writeLock().lock();
        try {
            List<Expense> updatedExpenses = new ArrayList<>(expenses);
            int existingIndex = findIndexById(updatedExpenses, expense.getId());
            if (existingIndex >= 0) {
                updatedExpenses.set(existingIndex, expense);
            } else {
                updatedExpenses.add(expense);
            }
            persistExpenses(updatedExpenses);
            expenses = updatedExpenses;
            return expense;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Expense> findAll() {
        lock.readLock().lock();
        try {
            return List.copyOf(expenses);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Expense> findByCategory(String category) {
        lock.readLock().lock();
        try {
            return expenses.stream()
                    .filter(expense -> Objects.equals(expense.getCategory(), category))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        lock.writeLock().lock();
        try {
            List<Expense> updatedExpenses = new ArrayList<>(expenses);
            boolean deleted = updatedExpenses.removeIf(expense -> Objects.equals(expense.getId(), id));
            if (deleted) {
                persistExpenses(updatedExpenses);
                expenses = updatedExpenses;
            }
            return deleted;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Expense> findById(UUID id) {
        lock.readLock().lock();
        try {
            return expenses.stream()
                    .filter(expense -> Objects.equals(expense.getId(), id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    private void initializeStorageFile() {
        try {
            Path parent = storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(storagePath)) {
                try {
                    Files.writeString(storagePath, "[]", StandardOpenOption.CREATE_NEW);
                } catch (FileAlreadyExistsException ignored) {
                    // Another process created the storage file between the existence check and creation.
                }
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to initialize expense storage", exception);
        }
    }

    private List<Expense> readExpenses() {
        try {
            List<Expense> loadedExpenses = objectMapper.readValue(storagePath.toFile(), EXPENSE_LIST_TYPE);
            return new ArrayList<>(loadedExpenses == null ? List.of() : loadedExpenses);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read expense storage", exception);
        }
    }

    private void persistExpenses(List<Expense> updatedExpenses) {
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile(storagePath.getParent(), "expenses-", ".json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(temporaryFile.toFile(), updatedExpenses);
            replaceStorageFile(temporaryFile);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to persist expense storage", exception);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    private void replaceStorageFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, storagePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, storagePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ignored) {
            // A failed cleanup does not affect the already persisted repository state.
        }
    }

    private int findIndexById(List<Expense> source, UUID id) {
        for (int index = 0; index < source.size(); index++) {
            if (Objects.equals(source.get(index).getId(), id)) {
                return index;
            }
        }
        return -1;
    }
}
}
