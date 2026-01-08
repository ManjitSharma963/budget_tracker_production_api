package com.expensetracker.repository;

import com.expensetracker.entity.Party;
import com.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    List<Party> findByUser(User user);
    Optional<Party> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
    List<Party> findByUserAndNameContainingIgnoreCase(User user, String name);
}

