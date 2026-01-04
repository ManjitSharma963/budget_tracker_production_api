package com.expensetracker.service;

import com.expensetracker.entity.Income;
import com.expensetracker.entity.User;
import com.expensetracker.repository.IncomeRepository;
import com.expensetracker.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public IncomeService(IncomeRepository incomeRepository, SecurityUtil securityUtil) {
        this.incomeRepository = incomeRepository;
        this.securityUtil = securityUtil;
    }

    public List<Income> getAllIncome() {
        User currentUser = securityUtil.getCurrentUser();
        return incomeRepository.findByUser(currentUser);
    }

    public Optional<Income> getIncomeById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return incomeRepository.findByIdAndUser(id, currentUser);
    }

    public Income createIncome(Income income) {
        User currentUser = securityUtil.getCurrentUser();
        income.setUser(currentUser);
        return incomeRepository.save(income);
    }

    public Income updateIncome(Long id, Income incomeDetails) {
        User currentUser = securityUtil.getCurrentUser();
        Income income = incomeRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + id));

        income.setAmount(incomeDetails.getAmount());
        income.setDescription(incomeDetails.getDescription());
        income.setSource(incomeDetails.getSource());

        return incomeRepository.save(income);
    }

    public void deleteIncome(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        if (!incomeRepository.existsByIdAndUser(id, currentUser)) {
            throw new RuntimeException("Income not found with id: " + id);
        }
        incomeRepository.deleteById(id);
    }
}

