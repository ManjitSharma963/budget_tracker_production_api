package com.expensetracker.service;

import com.expensetracker.entity.Note;
import com.expensetracker.entity.User;
import com.expensetracker.repository.NoteRepository;
import com.expensetracker.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final SecurityUtil securityUtil;

    @Autowired
    public NoteService(NoteRepository noteRepository, SecurityUtil securityUtil) {
        this.noteRepository = noteRepository;
        this.securityUtil = securityUtil;
    }

    public List<Note> getAllNotes() {
        User currentUser = securityUtil.getCurrentUser();
        return noteRepository.findByUser(currentUser);
    }

    public Optional<Note> getNoteById(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        return noteRepository.findByIdAndUser(id, currentUser);
    }

    public Note createNote(Note note) {
        User currentUser = securityUtil.getCurrentUser();
        note.setUser(currentUser);
        return noteRepository.save(note);
    }

    public Note updateNote(Long id, Note noteDetails) {
        User currentUser = securityUtil.getCurrentUser();
        Note note = noteRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        note.setTitle(noteDetails.getTitle());
        note.setNote(noteDetails.getNote());

        return noteRepository.save(note);
    }

    public void deleteNote(Long id) {
        User currentUser = securityUtil.getCurrentUser();
        if (!noteRepository.existsByIdAndUser(id, currentUser)) {
            throw new RuntimeException("Note not found with id: " + id);
        }
        noteRepository.deleteById(id);
    }
}

