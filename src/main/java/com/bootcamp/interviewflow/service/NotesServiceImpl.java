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

import java.util.Arrays;
import java.util.Collections;
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
        var application = applicationRepository.findById(request.applicationId()).orElseThrow(
                () -> new ApplicationNotFoundException("Application with id " + request.applicationId() + " not found"));
        String tagsString = request.tags() != null && !request.tags().isEmpty()
                ? String.join(",", request.tags())
                : null;
        var savedNote = noteRepository.save(new Note(application, request.title(), request.content(), tagsString));
        return new NoteResponse(
                savedNote.getId(),
                application.getId(),
                savedNote.getTitle(),
                savedNote.getContent(),
                tagsString != null ? Arrays.asList(tagsString.split(",")) : Collections.emptyList(),
                savedNote.getCreatedAt(),
                savedNote.getUpdatedAt()
        );
    }

    @Override
    public NoteResponse getById(Long id) {
        var note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        return new NoteResponse(
                note.getId(),
                note.getApplication().getId(),
                note.getTitle(),
                note.getContent(),
                note.getTags() != null && !note.getTags().isEmpty()
                        ? Arrays.asList(note.getTags().split(","))
                        : Collections.emptyList(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    @Override
    public List<NoteResponse> getAllByApplicationId(Long applicationId) {
        List<Note> notes = noteRepository.findAllByApplication_Id(applicationId);
        return notes.stream()
                .map(note -> new NoteResponse(
                        note.getId(),
                        note.getApplication().getId(),
                        note.getTitle(),
                        note.getContent(),
                        note.getTags() != null && !note.getTags().isEmpty()
                                ? Arrays.asList(note.getTags().split(","))
                                : Collections.emptyList(),
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
