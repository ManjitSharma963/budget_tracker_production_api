package com.expensetracker.controller;

import com.expensetracker.dto.LedgerEntryDto;
import com.expensetracker.dto.LedgerSummary;
import com.expensetracker.dto.OutstandingBalanceResponse;
import com.expensetracker.entity.LedgerEntry;
import com.expensetracker.service.LedgerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    @Autowired
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/entries")
    public ResponseEntity<LedgerEntryDto> createLedgerEntry(@Valid @RequestBody LedgerEntry ledgerEntry) {
        try {
            LedgerEntry createdEntry = ledgerService.createLedgerEntry(ledgerEntry);
            return ResponseEntity.status(HttpStatus.CREATED).body(LedgerEntryDto.fromEntity(createdEntry));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<LedgerEntryDto> getLedgerEntryById(@PathVariable Long id) {
        return ledgerService.getLedgerEntryById(id)
                .map(entry -> ResponseEntity.ok(LedgerEntryDto.fromEntity(entry)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/entries/{id}")
    public ResponseEntity<LedgerEntryDto> updateLedgerEntry(@PathVariable Long id, @Valid @RequestBody LedgerEntry entryDetails) {
        try {
            LedgerEntry updatedEntry = ledgerService.updateLedgerEntry(id, entryDetails);
            return ResponseEntity.ok(LedgerEntryDto.fromEntity(updatedEntry));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteLedgerEntry(@PathVariable Long id) {
        try {
            ledgerService.deleteLedgerEntry(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/parties/{partyId}/entries")
    public ResponseEntity<List<LedgerEntryDto>> getLedgerEntriesByParty(@PathVariable Long partyId) {
        try {
            List<LedgerEntry> entries = ledgerService.getLedgerEntriesByParty(partyId);
            List<LedgerEntryDto> dtos = entries.stream()
                    .map(LedgerEntryDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/parties/{partyId}/entries/date-range")
    public ResponseEntity<List<LedgerEntryDto>> getLedgerEntriesByDateRange(
            @PathVariable Long partyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<LedgerEntry> entries = ledgerService.getLedgerEntriesByPartyAndDateRange(partyId, startDate, endDate);
            List<LedgerEntryDto> dtos = entries.stream()
                    .map(LedgerEntryDto::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/parties/{partyId}/summary")
    public ResponseEntity<LedgerSummary> getPartyLedgerSummary(@PathVariable Long partyId) {
        try {
            LedgerSummary summary = ledgerService.getPartyLedgerSummary(partyId);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/parties/{partyId}/outstanding")
    public ResponseEntity<OutstandingBalanceResponse> getPartyOutstandingBalance(@PathVariable Long partyId) {
        try {
            BigDecimal balance = ledgerService.getPartyOutstandingBalance(partyId);
            OutstandingBalanceResponse response = new OutstandingBalanceResponse(partyId, balance);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

