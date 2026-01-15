package com.expensetracker.controller;

import com.expensetracker.dto.PageResponse;
import com.expensetracker.entity.Expense;
import com.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Expense management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @Operation(summary = "Get all expenses", description = "Retrieve all expenses for the authenticated user. Supports pagination.")
    public ResponseEntity<?> getAllExpenses(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "desc") @RequestParam(defaultValue = "desc") String sortDir) {
        
        if (size > 0) {
            Page<Expense> expensePage = expenseService.getAllExpenses(page, size, sortBy, sortDir);
            PageResponse<Expense> response = PageResponse.of(
                    expensePage.getContent(),
                    expensePage.getNumber(),
                    expensePage.getSize(),
                    expensePage.getTotalElements()
            );
            return ResponseEntity.ok(response);
        } else {
            List<Expense> expenses = expenseService.getAllExpenses();
            return ResponseEntity.ok(expenses);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expense by ID", description = "Retrieve a specific expense by its ID")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }

    @PostMapping
    @Operation(summary = "Create expense", description = "Create a new expense")
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update expense", description = "Update an existing expense")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expenseDetails) {
        Expense updatedExpense = expenseService.updateExpense(id, expenseDetails);
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense", description = "Delete an expense by ID")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}

