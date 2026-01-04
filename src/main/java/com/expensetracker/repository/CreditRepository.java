package com.expensetracker.repository;

import com.expensetracker.entity.Credit;
import com.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByUser(User user);
    Optional<Credit> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
}

