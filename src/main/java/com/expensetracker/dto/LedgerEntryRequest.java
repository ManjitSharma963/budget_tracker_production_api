package com.expensetracker.dto;

import com.expensetracker.entity.LedgerEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntryRequest {
    
    // Support both "partyId" and nested "party.id"
    private Long partyId;
    
    private PartyIdWrapper party;
    
    @NotNull(message = "Transaction type is required")
    private LedgerEntry.TransactionType transactionType;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;
    
    private String description;
    
    private String referenceNumber;
    
    private String paymentMode;
    
    // Helper method to get party ID from either format
    public Long getPartyIdValue() {
        if (partyId != null) {
            return partyId;
        }
        if (party != null && party.getId() != null) {
            return party.getId();
        }
        return null;
    }
    
    // Nested class for party object format
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyIdWrapper {
        private Long id;
    }
}

