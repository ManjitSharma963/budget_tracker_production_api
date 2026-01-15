package com.expensetracker.service;

import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.entity.Budget;
import com.expensetracker.entity.User;
import com.expensetracker.exception.DuplicateResourceException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, SecurityUtil securityUtil) {
        this.budgetRepository = budgetRepository;
        this.securityUtil = securityUtil;
    }

    public List<Budget> getAllBudgets() {
        User currentUser = securityUtil.getCurrentUser();
        return budgetRepository.findByUser(currentUser);
    }

    public Page<Budget> getAllBudgets(int page, int size, String sortBy, String sortDir) {
        User currentUser = securityUtil.getCurrentUser();
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return budgetRepository.findByUser(currentUser, pageable);
    }

    public Budget getBudgetById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return budgetRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));
    }

    public Budget createBudget(BudgetRequest budgetRequest) {
        User currentUser = securityUtil.getCurrentUser();

        // Validate required fields
        if (budgetRequest.getCategory() == null || budgetRequest.getCategory().trim().isEmpty() || 
            budgetRequest.getBudgetType() == null || budgetRequest.getPeriod() == null) {
            throw new ValidationException("Missing required fields: category, budgetType, period");
        }

        String category = budgetRequest.getCategory().trim();
        
        // Check if budget already exists for this category and period
        if (budgetRepository.existsByUserAndCategoryAndPeriod(currentUser, category, budgetRequest.getPeriod())) {
            throw new DuplicateResourceException("Budget already exists for this category and period");
        }

        Budget budget = budgetRequest.toBudget();

        // Validate and set amount/percentage based on budget type
        if (budgetRequest.getBudgetType() == Budget.BudgetType.fixed) {
            if (budgetRequest.getAmount() == null || budgetRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Amount must be greater than 0 for fixed budget");
            }
            budget.setAmount(budgetRequest.getAmount());
            budget.setPercentage(null);
        } else if (budgetRequest.getBudgetType() == Budget.BudgetType.percentage) {
            if (budgetRequest.getPercentage() == null) {
                throw new ValidationException("Percentage is required for percentage budget");
            }
            if (budgetRequest.getPercentage().compareTo(BigDecimal.ZERO) < 0 || 
                budgetRequest.getPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new ValidationException("Percentage must be between 0 and 100");
            }
            budget.setPercentage(budgetRequest.getPercentage());
            
            // Calculate amount from percentage and monthly income
            if (budgetRequest.getMonthlyIncome() != null && budgetRequest.getMonthlyIncome().compareTo(BigDecimal.ZERO) > 0) {
                budget.calculateAmountFromPercentage(budgetRequest.getMonthlyIncome());
            } else {
                // If monthlyIncome not provided, set amount to 0 (can be updated later)
                budget.setAmount(BigDecimal.ZERO);
            }
        }

        budget.setUser(currentUser);
        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long id, BudgetRequest budgetRequest) {
        User currentUser = securityUtil.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", id));

        // Determine final category and period values
        String finalCategory = budgetRequest.getCategory() != null ? 
                budgetRequest.getCategory().trim() : budget.getCategory();
        Budget.BudgetPeriod finalPeriod = budgetRequest.getPeriod() != null ? 
                budgetRequest.getPeriod() : budget.getPeriod();

        // Check if changing category/period would create a duplicate
        if (!budget.getCategory().equals(finalCategory) || !budget.getPeriod().equals(finalPeriod)) {
            Optional<Budget> existing = budgetRepository.findByUserAndCategoryAndPeriod(
                    currentUser, finalCategory, finalPeriod);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new DuplicateResourceException("Budget already exists for this category and period");
            }
        }

        // Update category and period
        if (budgetRequest.getCategory() != null) {
            budget.setCategory(budgetRequest.getCategory().trim());
        }
        if (budgetRequest.getPeriod() != null) {
            budget.setPeriod(budgetRequest.getPeriod());
        }

        // Handle budget type change
        Budget.BudgetType newBudgetType = budgetRequest.getBudgetType() != null ? 
                budgetRequest.getBudgetType() : budget.getBudgetType();

        if (budgetRequest.getBudgetType() != null) {
            budget.setBudgetType(newBudgetType);
        }

        // Handle amount/percentage updates based on budget type
        if (newBudgetType == Budget.BudgetType.fixed) {
            if (budgetRequest.getAmount() != null) {
                if (budgetRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ValidationException("Amount must be greater than 0");
                }
                budget.setAmount(budgetRequest.getAmount());
            }
            // Clear percentage when switching to fixed
            if (budgetRequest.getBudgetType() != null && budgetRequest.getBudgetType() == Budget.BudgetType.fixed) {
                budget.setPercentage(null);
            }
        } else if (newBudgetType == Budget.BudgetType.percentage) {
            if (budgetRequest.getPercentage() != null) {
                if (budgetRequest.getPercentage().compareTo(BigDecimal.ZERO) < 0 || 
                    budgetRequest.getPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new ValidationException("Percentage must be between 0 and 100");
                }
                budget.setPercentage(budgetRequest.getPercentage());
                // Recalculate amount if monthlyIncome provided
                if (budgetRequest.getMonthlyIncome() != null && 
                    budgetRequest.getMonthlyIncome().compareTo(BigDecimal.ZERO) > 0) {
                    budget.calculateAmountFromPercentage(budgetRequest.getMonthlyIncome());
                }
            }
        }

        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        if (!budgetRepository.existsByIdAndUser(id, currentUser)) {
            throw new ResourceNotFoundException("Budget", id);
        }
        budgetRepository.deleteById(id);
    }
}

