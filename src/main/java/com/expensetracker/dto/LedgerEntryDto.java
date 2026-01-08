package com.expensetracker.dto;

import com.expensetracker.entity.LedgerEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntryDto {
    private Long id;
    private Long partyId;
    private String partyName;
    private LedgerEntry.TransactionType transactionType;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private String referenceNumber;
    private String paymentMode;
    private BigDecimal runningBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LedgerEntryDto fromEntity(LedgerEntry entry) {
        LedgerEntryDto dto = new LedgerEntryDto();
        dto.setId(entry.getId());
        dto.setPartyId(entry.getParty().getId());
        dto.setPartyName(entry.getParty().getName());
        dto.setTransactionType(entry.getTransactionType());
        dto.setAmount(entry.getAmount());
        dto.setTransactionDate(entry.getTransactionDate());
        dto.setDescription(entry.getDescription());
        dto.setReferenceNumber(entry.getReferenceNumber());
        dto.setPaymentMode(entry.getPaymentMode());
        dto.setRunningBalance(entry.getRunningBalance());
        dto.setCreatedAt(entry.getCreatedAt());
        dto.setUpdatedAt(entry.getUpdatedAt());
        return dto;
    }
}

