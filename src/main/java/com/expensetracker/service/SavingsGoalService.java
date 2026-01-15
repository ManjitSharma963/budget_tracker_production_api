package com.expensetracker.service;

import com.expensetracker.dto.SavingsGoalRequest;
import com.expensetracker.entity.SavingsGoal;
import com.expensetracker.entity.User;
import com.expensetracker.repository.SavingsGoalRepository;
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
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository, SecurityUtil securityUtil) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.securityUtil = securityUtil;
    }

    public List<SavingsGoal> getAllSavingsGoals() {
        User currentUser = securityUtil.getCurrentUser();
        return savingsGoalRepository.findByUser(currentUser);
    }

    public Page<SavingsGoal> getAllSavingsGoals(int page, int size, String sortBy, String sortDir) {
        User currentUser = securityUtil.getCurrentUser();
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return savingsGoalRepository.findByUser(currentUser, pageable);
    }

    public Optional<SavingsGoal> getSavingsGoalById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return savingsGoalRepository.findByIdAndUser(id, currentUser);
    }

    public SavingsGoal createSavingsGoal(SavingsGoalRequest savingsGoalRequest) {
        User currentUser = securityUtil.getCurrentUser();

        // Validate required fields
        if (savingsGoalRequest.getName() == null || savingsGoalRequest.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (savingsGoalRequest.getTargetAmount() == null || savingsGoalRequest.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Target amount must be greater than 0");
        }

        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setName(savingsGoalRequest.getName().trim());
        savingsGoal.setTargetAmount(savingsGoalRequest.getTargetAmount());
        savingsGoal.setCurrentAmount(savingsGoalRequest.getCurrentAmount() != null ? 
                savingsGoalRequest.getCurrentAmount() : BigDecimal.ZERO);
        savingsGoal.setTargetDate(savingsGoalRequest.getTargetDate());
        savingsGoal.setDescription(savingsGoalRequest.getDescription());
        savingsGoal.setUser(currentUser);

        return savingsGoalRepository.save(savingsGoal);
    }

    public SavingsGoal updateSavingsGoal(Long id, SavingsGoalRequest savingsGoalRequest) {
        User currentUser = securityUtil.getCurrentUser();
        SavingsGoal savingsGoal = savingsGoalRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));

        // Update only provided fields
        if (savingsGoalRequest.getName() != null && !savingsGoalRequest.getName().trim().isEmpty()) {
            savingsGoal.setName(savingsGoalRequest.getName().trim());
        }
        if (savingsGoalRequest.getTargetAmount() != null) {
            if (savingsGoalRequest.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Target amount must be greater than 0");
            }
            savingsGoal.setTargetAmount(savingsGoalRequest.getTargetAmount());
        }
        if (savingsGoalRequest.getCurrentAmount() != null) {
            if (savingsGoalRequest.getCurrentAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Current amount cannot be negative");
            }
            savingsGoal.setCurrentAmount(savingsGoalRequest.getCurrentAmount());
        }
        if (savingsGoalRequest.getTargetDate() != null) {
            savingsGoal.setTargetDate(savingsGoalRequest.getTargetDate());
        }
        if (savingsGoalRequest.getDescription() != null) {
            savingsGoal.setDescription(savingsGoalRequest.getDescription());
        }

        return savingsGoalRepository.save(savingsGoal);
    }

    public void deleteSavingsGoal(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        if (!savingsGoalRepository.existsByIdAndUser(id, currentUser)) {
            throw new RuntimeException("Savings goal not found");
        }
        savingsGoalRepository.deleteById(id);
    }
}

