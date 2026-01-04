package com.expensetracker.repository;

import com.expensetracker.entity.Income;
import com.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);
    Optional<Income> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
}

