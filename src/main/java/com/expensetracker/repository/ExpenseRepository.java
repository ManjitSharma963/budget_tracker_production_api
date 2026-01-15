package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    
    @EntityGraph(attributePaths = {"user"})
    Page<Expense> findByUser(User user, Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.category = :category")
    Page<Expense> findByUserAndCategory(@Param("user") User user, @Param("category") String category, Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.createdAt >= :startDate AND e.createdAt <= :endDate")
    Page<Expense> findByUserAndDateRange(@Param("user") User user, 
                                         @Param("startDate") java.time.LocalDateTime startDate,
                                         @Param("endDate") java.time.LocalDateTime endDate, 
                                         Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    Optional<Expense> findByIdAndUser(Long id, User user);
    
    boolean existsByIdAndUser(Long id, User user);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user")
    Optional<BigDecimal> getTotalExpensesByUser(@Param("user") User user);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.category = :category")
    Optional<BigDecimal> getTotalExpensesByUserAndCategory(@Param("user") User user, @Param("category") String category);
}

