package com.expensetracker.controller;

import com.expensetracker.dto.PageResponse;
import com.expensetracker.dto.SavingsGoalRequest;
import com.expensetracker.entity.SavingsGoal;
import com.expensetracker.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/savings-goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @Autowired
    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @GetMapping
    public ResponseEntity<?> getAllSavingsGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        if (size > 0) {
            org.springframework.data.domain.Page<SavingsGoal> goalPage = savingsGoalService.getAllSavingsGoals(page, size, sortBy, sortDir);
            PageResponse<SavingsGoal> response = PageResponse.of(
                    goalPage.getContent(),
                    goalPage.getNumber(),
                    goalPage.getSize(),
                    goalPage.getTotalElements()
            );
            return ResponseEntity.ok(response);
        } else {
            List<SavingsGoal> goals = savingsGoalService.getAllSavingsGoals();
            return ResponseEntity.ok(goals);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSavingsGoalById(@PathVariable Long id) {
        try {
            Optional<SavingsGoal> goal = savingsGoalService.getSavingsGoalById(id);
            if (goal.isPresent()) {
                return ResponseEntity.ok(goal.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Savings goal not found");
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
    public ResponseEntity<?> createSavingsGoal(@RequestBody SavingsGoalRequest savingsGoalRequest) {
        try {
            // Validate required fields
            if (savingsGoalRequest.getName() == null || savingsGoalRequest.getName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Name is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (savingsGoalRequest.getTargetAmount() == null || 
                savingsGoalRequest.getTargetAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Target amount must be greater than 0");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            SavingsGoal createdGoal = savingsGoalService.createSavingsGoal(savingsGoalRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            if (e.getMessage() != null && e.getMessage().contains("not authenticated")) {
                error.put("error", "Invalid or missing authentication token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            } else if (e.getMessage() != null && (e.getMessage().contains("required") || 
                       e.getMessage().contains("must be"))) {
                error.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            } else {
                error.put("error", "Error creating savings goal");
                error.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error creating savings goal");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSavingsGoal(@PathVariable Long id, @RequestBody SavingsGoalRequest savingsGoalRequest) {
        try {
            // Validate amounts if provided
            if (savingsGoalRequest.getTargetAmount() != null && 
                savingsGoalRequest.getTargetAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Target amount must be greater than 0");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (savingsGoalRequest.getCurrentAmount() != null && 
                savingsGoalRequest.getCurrentAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Current amount cannot be negative");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            SavingsGoal updatedGoal = savingsGoalService.updateSavingsGoal(id, savingsGoalRequest);
            return ResponseEntity.ok(updatedGoal);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            if (e.getMessage() != null && e.getMessage().contains("not authenticated")) {
                error.put("error", "Invalid or missing authentication token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            } else if (e.getMessage() != null && e.getMessage().contains("not found")) {
                error.put("error", "Savings goal not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            } else if (e.getMessage() != null && (e.getMessage().contains("must be") || 
                       e.getMessage().contains("cannot be"))) {
                error.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            } else {
                error.put("error", "Error updating savings goal");
                error.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error updating savings goal");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavingsGoal(@PathVariable Long id) {
        try {
            savingsGoalService.deleteSavingsGoal(id);
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

