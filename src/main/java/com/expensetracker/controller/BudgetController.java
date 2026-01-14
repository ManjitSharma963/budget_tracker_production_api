package com.expensetracker.controller;

import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.entity.Budget;
import com.expensetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<?> getAllBudgets() {
        try {
            List<Budget> budgets = budgetService.getAllBudgets();
            return ResponseEntity.ok(budgets);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not authenticated")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or missing authentication token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetById(@PathVariable Long id) {
        try {
            Optional<Budget> budget = budgetService.getBudgetById(id);
            if (budget.isPresent()) {
                return ResponseEntity.ok(budget.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Budget not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not authenticated")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or missing authentication token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<?> createBudget(@RequestBody BudgetRequest budgetRequest) {
        try {
            // Validate required fields
            if (budgetRequest.getCategory() == null || budgetRequest.getBudgetType() == null || budgetRequest.getPeriod() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Missing required fields: category, budgetType, period");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Validate based on budget type
            if (budgetRequest.getBudgetType() == Budget.BudgetType.fixed) {
                if (budgetRequest.getAmount() == null || budgetRequest.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Amount must be greater than 0");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                }
            } else if (budgetRequest.getBudgetType() == Budget.BudgetType.percentage) {
                if (budgetRequest.getPercentage() == null) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Percentage is required for percentage budget");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                }
                if (budgetRequest.getPercentage().compareTo(java.math.BigDecimal.ZERO) < 0 || 
                    budgetRequest.getPercentage().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Percentage must be between 0 and 100");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                }
            }

            Budget createdBudget = budgetService.createBudget(budgetRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBudget);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            if (e.getMessage() != null && e.getMessage().contains("not authenticated")) {
                error.put("error", "Invalid or missing authentication token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            } else if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                error.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            } else if (e.getMessage() != null && (e.getMessage().contains("Missing required fields") || 
                       e.getMessage().contains("must be") || 
                       e.getMessage().contains("required"))) {
                error.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            } else {
                error.put("error", "Error creating budget");
                error.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error creating budget");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id, @RequestBody BudgetRequest budgetRequest) {
        try {
            // Validate percentage if provided
            if (budgetRequest.getPercentage() != null) {
                if (budgetRequest.getPercentage().compareTo(java.math.BigDecimal.ZERO) < 0 || 
                    budgetRequest.getPercentage().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Percentage must be between 0 and 100");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                }
            }

            // Validate amount if provided
            if (budgetRequest.getAmount() != null && budgetRequest.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Amount must be greater than 0");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            Budget updatedBudget = budgetService.updateBudget(id, budgetRequest);
            return ResponseEntity.ok(updatedBudget);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            if (e.getMessage() != null && e.getMessage().contains("not authenticated")) {
                error.put("error", "Invalid or missing authentication token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            } else if (e.getMessage() != null && e.getMessage().contains("not found")) {
                error.put("error", "Budget not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            } else if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                error.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            } else if (e.getMessage() != null && (e.getMessage().contains("must be") || e.getMessage().contains("required"))) {
                error.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            } else {
                error.put("error", "Error updating budget");
                error.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error updating budget");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        try {
            budgetService.deleteBudget(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not authenticated")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or missing authentication token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            } else if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}

