package com.expensetracker.service;

import com.expensetracker.dto.LedgerEntryDto;
import com.expensetracker.dto.LedgerSummary;
import com.expensetracker.entity.LedgerEntry;
import com.expensetracker.entity.Party;
import com.expensetracker.entity.User;
import com.expensetracker.repository.LedgerEntryRepository;
import com.expensetracker.repository.PartyRepository;
import com.expensetracker.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final PartyRepository partyRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public LedgerService(LedgerEntryRepository ledgerEntryRepository, 
                        PartyRepository partyRepository, 
                        SecurityUtil securityUtil) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.partyRepository = partyRepository;
        this.securityUtil = securityUtil;
    }

    public LedgerEntry createLedgerEntry(LedgerEntry ledgerEntry) {
        User currentUser = securityUtil.getCurrentUser();
        
        // Verify party belongs to current user
        Party party = partyRepository.findByIdAndUser(ledgerEntry.getParty().getId(), currentUser)
                .orElseThrow(() -> new RuntimeException("Party not found or access denied"));
        
        ledgerEntry.setParty(party);
        ledgerEntry.setUser(currentUser);
        
        if (ledgerEntry.getTransactionDate() == null) {
            ledgerEntry.setTransactionDate(LocalDate.now());
        }
        
        // Save entry first to get ID for ordering
        LedgerEntry savedEntry = ledgerEntryRepository.save(ledgerEntry);
        
        // Recalculate running balances for all entries of this party
        recalculateRunningBalances(party);
        
        return ledgerEntryRepository.findById(savedEntry.getId()).orElse(savedEntry);
    }

    public LedgerEntry updateLedgerEntry(Long id, LedgerEntry entryDetails) {
        User currentUser = securityUtil.getCurrentUser();
        LedgerEntry entry = ledgerEntryRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Ledger entry not found with id: " + id));

        // Verify party belongs to current user if changed
        if (entryDetails.getParty() != null && !entry.getParty().getId().equals(entryDetails.getParty().getId())) {
            Party party = partyRepository.findByIdAndUser(entryDetails.getParty().getId(), currentUser)
                    .orElseThrow(() -> new RuntimeException("Party not found or access denied"));
            entry.setParty(party);
        }

        entry.setTransactionType(entryDetails.getTransactionType());
        entry.setAmount(entryDetails.getAmount());
        entry.setTransactionDate(entryDetails.getTransactionDate());
        entry.setDescription(entryDetails.getDescription());
        entry.setReferenceNumber(entryDetails.getReferenceNumber());
        entry.setPaymentMode(entryDetails.getPaymentMode());

        LedgerEntry savedEntry = ledgerEntryRepository.save(entry);
        
        // Recalculate running balances
        recalculateRunningBalances(entry.getParty());
        
        return savedEntry;
    }

    public void deleteLedgerEntry(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        LedgerEntry entry = ledgerEntryRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Ledger entry not found with id: " + id));
        
        Party party = entry.getParty();
        ledgerEntryRepository.delete(entry);
        
        // Recalculate running balances after deletion
        recalculateRunningBalances(party);
    }

    public List<LedgerEntry> getLedgerEntriesByParty(Long partyId) {
        User currentUser = securityUtil.getCurrentUser();
        Party party = partyRepository.findByIdAndUser(partyId, currentUser)
                .orElseThrow(() -> new RuntimeException("Party not found or access denied"));
        
        return ledgerEntryRepository.findByPartyOrderByTransactionDateAscIdAsc(party);
    }

    public List<LedgerEntry> getLedgerEntriesByPartyAndDateRange(Long partyId, LocalDate startDate, LocalDate endDate) {
        User currentUser = securityUtil.getCurrentUser();
        Party party = partyRepository.findByIdAndUser(partyId, currentUser)
                .orElseThrow(() -> new RuntimeException("Party not found or access denied"));
        
        return ledgerEntryRepository.findByPartyAndTransactionDateBetween(party, startDate, endDate);
    }

    public LedgerSummary getPartyLedgerSummary(Long partyId) {
        User currentUser = securityUtil.getCurrentUser();
        Party party = partyRepository.findByIdAndUser(partyId, currentUser)
                .orElseThrow(() -> new RuntimeException("Party not found or access denied"));
        
        List<LedgerEntry> entries = ledgerEntryRepository.findByPartyOrderByTransactionDateAscIdAsc(party);
        
        BigDecimal totalPurchases = ledgerEntryRepository.getTotalPurchases(party);
        BigDecimal totalPayments = ledgerEntryRepository.getTotalPayments(party);
        BigDecimal openingBalance = party.getOpeningBalance() != null ? party.getOpeningBalance() : BigDecimal.ZERO;
        
        // Outstanding Balance = Opening Balance + Total Purchases - Total Payments
        BigDecimal outstandingBalance = openingBalance
                .add(totalPurchases != null ? totalPurchases : BigDecimal.ZERO)
                .subtract(totalPayments != null ? totalPayments : BigDecimal.ZERO);
        
        List<LedgerEntryDto> transactionDtos = entries.stream()
                .map(LedgerEntryDto::fromEntity)
                .collect(Collectors.toList());
        
        return new LedgerSummary(
                party.getId(),
                party.getName(),
                openingBalance,
                totalPurchases != null ? totalPurchases : BigDecimal.ZERO,
                totalPayments != null ? totalPayments : BigDecimal.ZERO,
                outstandingBalance,
                entries.size(),
                transactionDtos
        );
    }

    public BigDecimal getPartyOutstandingBalance(Long partyId) {
        User currentUser = securityUtil.getCurrentUser();
        Party party = partyRepository.findByIdAndUser(partyId, currentUser)
                .orElseThrow(() -> new RuntimeException("Party not found or access denied"));
        
        BigDecimal totalPurchases = ledgerEntryRepository.getTotalPurchases(party);
        BigDecimal totalPayments = ledgerEntryRepository.getTotalPayments(party);
        BigDecimal openingBalance = party.getOpeningBalance() != null ? party.getOpeningBalance() : BigDecimal.ZERO;
        
        return openingBalance
                .add(totalPurchases != null ? totalPurchases : BigDecimal.ZERO)
                .subtract(totalPayments != null ? totalPayments : BigDecimal.ZERO);
    }

    /**
     * Recalculates running balances for all entries of a party
     * Running balance = Opening Balance + Sum of all previous transactions
     */
    private void recalculateRunningBalances(Party party) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByPartyOrderByTransactionDateAscIdAsc(party);
        BigDecimal openingBalance = party.getOpeningBalance() != null ? party.getOpeningBalance() : BigDecimal.ZERO;
        BigDecimal runningBalance = openingBalance;
        
        for (LedgerEntry entry : entries) {
            switch (entry.getTransactionType()) {
                case PURCHASE:
                case ADJUSTMENT:
                    runningBalance = runningBalance.add(entry.getAmount());
                    break;
                case PAYMENT:
                    runningBalance = runningBalance.subtract(entry.getAmount());
                    break;
            }
            entry.setRunningBalance(runningBalance);
            ledgerEntryRepository.save(entry);
        }
    }

    public Optional<LedgerEntry> getLedgerEntryById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return ledgerEntryRepository.findByIdAndUser(id, currentUser);
    }
}

