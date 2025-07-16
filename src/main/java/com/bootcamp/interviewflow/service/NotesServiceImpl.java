package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.NoteRequest;
import com.bootcamp.interviewflow.dto.NoteResponse;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.exception.NoteNotFoundException;
import com.bootcamp.interviewflow.model.Note;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.NoteRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesServiceImpl implements NotesService {
    private final ApplicationRepository applicationRepository;
    private final NoteRepository noteRepository;

    public NotesServiceImpl(ApplicationRepository applicationRepository,
                            NoteRepository noteRepository) {
        this.applicationRepository = applicationRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public NoteResponse create(NoteRequest request) {
        var application = applicationRepository.findById(request.applicationId())
                .orElseThrow(() -> new ApplicationNotFoundException(request.applicationId()));
        var savedNote = noteRepository.save(new Note(application, request.content()));
        return new NoteResponse(savedNote.getId(),
                application.getId(),
                savedNote.getContent(),
                savedNote.getCreatedAt(),
                savedNote.getUpdatedAt());
    }

    @Override
    public NoteResponse getById(Long id) {
        var note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        return new NoteResponse(note.getId(),
                note.getApplication().getId(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt());
    }

    @Override
    public List<NoteResponse> getAll() {
        List<Note> notes = noteRepository.findAll();
        return notes.stream()
                .map(note -> new NoteResponse(
                        note.getId(),
                        note.getApplication().getId(),
                        note.getContent(),
                        note.getCreatedAt(),
                        note.getUpdatedAt()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        try {
            noteRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoteNotFoundException(id);
        }
    }
}
