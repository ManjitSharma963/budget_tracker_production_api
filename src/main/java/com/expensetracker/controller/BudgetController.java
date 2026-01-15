package com.expensetracker.controller;

import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.dto.PageResponse;
import com.expensetracker.entity.Budget;
import com.expensetracker.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "Budget management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    @Operation(summary = "Get all budgets", description = "Retrieve all budgets for the authenticated user. Supports pagination.")
    public ResponseEntity<?> getAllBudgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        if (size > 0) {
            org.springframework.data.domain.Page<Budget> budgetPage = budgetService.getAllBudgets(page, size, sortBy, sortDir);
            PageResponse<Budget> response = PageResponse.of(
                    budgetPage.getContent(),
                    budgetPage.getNumber(),
                    budgetPage.getSize(),
                    budgetPage.getTotalElements()
            );
            return ResponseEntity.ok(response);
        } else {
            List<Budget> budgets = budgetService.getAllBudgets();
            return ResponseEntity.ok(budgets);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID", description = "Retrieve a specific budget by its ID")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Budget budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @PostMapping
    @Operation(summary = "Create budget", description = "Create a new budget (fixed or percentage)")
    public ResponseEntity<Budget> createBudget(@RequestBody BudgetRequest budgetRequest) {
        Budget createdBudget = budgetService.createBudget(budgetRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBudget);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget", description = "Update an existing budget (supports partial updates)")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody BudgetRequest budgetRequest) {
        Budget updatedBudget = budgetService.updateBudget(id, budgetRequest);
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget", description = "Delete a budget by ID")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
