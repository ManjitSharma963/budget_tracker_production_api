package com.expensetracker.controller;

import com.expensetracker.entity.Party;
import com.expensetracker.service.PartyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parties")
public class PartyController {

    private final PartyService partyService;

    @Autowired
    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @GetMapping
    public ResponseEntity<List<Party>> getAllParties() {
        List<Party> parties = partyService.getAllParties();
        return ResponseEntity.ok(parties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Party> getPartyById(@PathVariable Long id) {
        return partyService.getPartyById(id)
                .map(party -> ResponseEntity.ok(party))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Party>> searchParties(@RequestParam String q) {
        List<Party> parties = partyService.searchParties(q);
        return ResponseEntity.ok(parties);
    }

    @PostMapping
    public ResponseEntity<Party> createParty(@Valid @RequestBody Party party) {
        try {
            Party createdParty = partyService.createParty(party);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdParty);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Party> updateParty(@PathVariable Long id, @Valid @RequestBody Party partyDetails) {
        try {
            Party updatedParty = partyService.updateParty(id, partyDetails);
            return ResponseEntity.ok(updatedParty);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParty(@PathVariable Long id) {
        try {
            partyService.deleteParty(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

