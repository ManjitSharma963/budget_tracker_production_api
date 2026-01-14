package com.expensetracker.dto;

import com.expensetracker.entity.Budget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {

    private String category;
    private Budget.BudgetType budgetType;
    private BigDecimal amount;
    private BigDecimal percentage;
    private Budget.BudgetPeriod period;
    private BigDecimal monthlyIncome; // Used for calculating amount from percentage

    public Budget toBudget() {
        Budget budget = new Budget();
        budget.setCategory(this.category != null ? this.category.trim() : null);
        budget.setBudgetType(this.budgetType);
        budget.setAmount(this.amount);
        budget.setPercentage(this.percentage);
        budget.setPeriod(this.period);
        return budget;
    }
}

