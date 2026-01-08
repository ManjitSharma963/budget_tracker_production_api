package com.expensetracker.repository;

import com.expensetracker.entity.LedgerEntry;
import com.expensetracker.entity.Party;
import com.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findByUser(User user);
    List<LedgerEntry> findByParty(Party party);
    List<LedgerEntry> findByPartyAndUser(Party party, User user);
    List<LedgerEntry> findByPartyOrderByTransactionDateAscIdAsc(Party party);
    Optional<LedgerEntry> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
    
    @Query("SELECT le FROM LedgerEntry le WHERE le.party = :party AND le.transactionDate BETWEEN :startDate AND :endDate ORDER BY le.transactionDate ASC, le.id ASC")
    List<LedgerEntry> findByPartyAndTransactionDateBetween(
        @Param("party") Party party,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT COALESCE(SUM(le.amount), 0) FROM LedgerEntry le WHERE le.party = :party AND le.transactionType = 'PURCHASE'")
    java.math.BigDecimal getTotalPurchases(@Param("party") Party party);
    
    @Query("SELECT COALESCE(SUM(le.amount), 0) FROM LedgerEntry le WHERE le.party = :party AND le.transactionType = 'PAYMENT'")
    java.math.BigDecimal getTotalPayments(@Param("party") Party party);
}

