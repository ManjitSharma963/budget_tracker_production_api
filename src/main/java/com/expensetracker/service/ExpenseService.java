package com.expensetracker.service;

import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, SecurityUtil securityUtil) {
        this.expenseRepository = expenseRepository;
        this.securityUtil = securityUtil;
    }

    public List<Expense> getAllExpenses() {
        User currentUser = securityUtil.getCurrentUser();
        return expenseRepository.findByUser(currentUser);
    }

    public Optional<Expense> getExpenseById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return expenseRepository.findByIdAndUser(id, currentUser);
    }

    public Expense createExpense(Expense expense) {
        User currentUser = securityUtil.getCurrentUser();
        expense.setUser(currentUser);
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long id, Expense expenseDetails) {
        User currentUser = securityUtil.getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        expense.setAmount(expenseDetails.getAmount());
        expense.setDescription(expenseDetails.getDescription());
        expense.setCategory(expenseDetails.getCategory());

        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        if (!expenseRepository.existsByIdAndUser(id, currentUser)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }
}

