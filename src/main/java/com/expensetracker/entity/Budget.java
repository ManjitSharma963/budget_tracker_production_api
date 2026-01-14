package com.expensetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "category", "period"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category is required")
    @Column(nullable = false, length = 100)
    private String category;

    @NotNull(message = "Budget type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "budget_type", nullable = false, length = 20)
    private BudgetType budgetType;

    @Positive(message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    @NotNull(message = "Period is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BudgetPeriod period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BudgetType {
        fixed,
        percentage
    }

    public enum BudgetPeriod {
        monthly,
        yearly
    }

    /**
     * Calculate amount from percentage and monthly income
     */
    public void calculateAmountFromPercentage(BigDecimal monthlyIncome) {
        if (this.budgetType == BudgetType.percentage && this.percentage != null && monthlyIncome != null) {
            this.amount = monthlyIncome
                    .multiply(this.percentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
    }
}

