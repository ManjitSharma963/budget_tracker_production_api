package com.expensetracker.service;

import com.expensetracker.entity.Credit;
import com.expensetracker.entity.User;
import com.expensetracker.repository.CreditRepository;
import com.expensetracker.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreditService {

    private final CreditRepository creditRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public CreditService(CreditRepository creditRepository, SecurityUtil securityUtil) {
        this.creditRepository = creditRepository;
        this.securityUtil = securityUtil;
    }

    public List<Credit> getAllCredits() {
        User currentUser = securityUtil.getCurrentUser();
        return creditRepository.findByUser(currentUser);
    }

    public Optional<Credit> getCreditById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return creditRepository.findByIdAndUser(id, currentUser);
    }

    public Credit createCredit(Credit credit) {
        User currentUser = securityUtil.getCurrentUser();
        credit.setUser(currentUser);
        return creditRepository.save(credit);
    }

    public Credit updateCredit(Long id, Credit creditDetails) {
        User currentUser = securityUtil.getCurrentUser();
        Credit credit = creditRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Credit not found with id: " + id));

        credit.setAmount(creditDetails.getAmount());
        credit.setDescription(creditDetails.getDescription());
        credit.setCreditor(creditDetails.getCreditor());
        credit.setCreditType(creditDetails.getCreditType());

        return creditRepository.save(credit);
    }

    public void deleteCredit(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        if (!creditRepository.existsByIdAndUser(id, currentUser)) {
            throw new RuntimeException("Credit not found with id: " + id);
        }
        creditRepository.deleteById(id);
    }
}

