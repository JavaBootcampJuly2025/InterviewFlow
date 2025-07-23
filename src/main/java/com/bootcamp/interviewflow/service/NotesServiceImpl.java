package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.NoteRequest;
import com.bootcamp.interviewflow.dto.NoteResponse;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.exception.NoteNotFoundException;
import com.bootcamp.interviewflow.model.Application;
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
        Application application = applicationRepository.findById(request.applicationId()).orElseThrow(
                () -> new ApplicationNotFoundException("Application with id " + request.applicationId() + " not found"));

        String tagsString = request.tags() != null && !request.tags().isEmpty()
                ? String.join(",", request.tags())
                : null;

        Note noteToSave = Note.builder()
                .application(application)
                .title(request.title())
                .content(request.content())
                .tags(tagsString)
                .build();

        Note savedNote = noteRepository.save(noteToSave);

        return NoteResponse.from(savedNote);
    }

    @Override
    public NoteResponse getById(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        return NoteResponse.from(note);
    }

    @Override
    public List<NoteResponse> getAllByApplicationId(Long applicationId) {
        List<Note> notes = noteRepository.findAllByApplication_Id(applicationId);
        return notes
                .stream()
                .map(NoteResponse::from)
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
