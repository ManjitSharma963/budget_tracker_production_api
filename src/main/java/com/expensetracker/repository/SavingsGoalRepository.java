package com.expensetracker.repository;

import com.expensetracker.entity.SavingsGoal;
import com.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
    
    @EntityGraph(attributePaths = {"user"})
    Page<SavingsGoal> findByUser(User user, Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    Optional<SavingsGoal> findByIdAndUser(Long id, User user);
    
    boolean existsByIdAndUser(Long id, User user);
}

