package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.NoteRequest;
import com.bootcamp.interviewflow.dto.NoteResponse;
import com.bootcamp.interviewflow.entity.Note;
import com.bootcamp.interviewflow.exception.NoteNotFoundException;
import com.bootcamp.interviewflow.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotesServiceImpl implements NotesService {
    private final NoteRepository repository;

    public NotesServiceImpl(NoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public NoteResponse create(NoteRequest request) {
        var savedNote = repository.save(new Note(request.content()));
        return new NoteResponse(savedNote.getId(),
                savedNote.getContent(),
                savedNote.getCreatedAt(),
                savedNote.getUpdatedAt());
    }

    @Override
    public NoteResponse getById(Long id) {
        var note = repository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        return new NoteResponse(note.getId(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt());
    }

    @Override
    public List<NoteResponse> getAll() {
        List<Note> notes = repository.findAll();
        return notes.stream()
                .map(note -> new NoteResponse(
                        note.getId(),
                        note.getContent(),
                        note.getCreatedAt(),
                        note.getUpdatedAt()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
