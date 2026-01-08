package com.expensetracker.service;

import com.expensetracker.entity.Party;
import com.expensetracker.entity.User;
import com.expensetracker.repository.PartyRepository;
import com.expensetracker.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PartyService {

    private final PartyRepository partyRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public PartyService(PartyRepository partyRepository, SecurityUtil securityUtil) {
        this.partyRepository = partyRepository;
        this.securityUtil = securityUtil;
    }

    public List<Party> getAllParties() {
        User currentUser = securityUtil.getCurrentUser();
        return partyRepository.findByUser(currentUser);
    }

    public Optional<Party> getPartyById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return partyRepository.findByIdAndUser(id, currentUser);
    }

    public Party createParty(Party party) {
        User currentUser = securityUtil.getCurrentUser();
        party.setUser(currentUser);
        if (party.getOpeningBalance() == null) {
            party.setOpeningBalance(java.math.BigDecimal.ZERO);
        }
        return partyRepository.save(party);
    }

    public Party updateParty(Long id, Party partyDetails) {
        User currentUser = securityUtil.getCurrentUser();
        Party party = partyRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Party not found with id: " + id));

        party.setName(partyDetails.getName());
        party.setPhone(partyDetails.getPhone());
        party.setNotes(partyDetails.getNotes());
        // Opening balance should not be updated directly - use adjustment entry instead
        // party.setOpeningBalance(partyDetails.getOpeningBalance());

        return partyRepository.save(party);
    }

    public void deleteParty(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        Party party = partyRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Party not found with id: " + id));
        
        // Check if party has ledger entries
        // In a production system, you might want to prevent deletion if entries exist
        // For now, we'll allow deletion (cascade will handle it if configured)
        
        partyRepository.delete(party);
    }

    public List<Party> searchParties(String searchTerm) {
        User currentUser = securityUtil.getCurrentUser();
        return partyRepository.findByUserAndNameContainingIgnoreCase(currentUser, searchTerm);
    }
}

