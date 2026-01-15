package com.expensetracker.repository;

import com.expensetracker.entity.Budget;
import com.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUser(User user);
    
    @EntityGraph(attributePaths = {"user"})
    Page<Budget> findByUser(User user, Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    Optional<Budget> findByIdAndUser(Long id, User user);
    
    boolean existsByIdAndUser(Long id, User user);
    Optional<Budget> findByUserAndCategoryAndPeriod(User user, String category, Budget.BudgetPeriod period);
    boolean existsByUserAndCategoryAndPeriod(User user, String category, Budget.BudgetPeriod period);
}

