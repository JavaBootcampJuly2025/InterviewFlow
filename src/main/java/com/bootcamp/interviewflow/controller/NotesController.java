package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.NoteRequest;
import com.bootcamp.interviewflow.dto.NoteResponse;
import com.bootcamp.interviewflow.service.NotesService;
import com.bootcamp.interviewflow.service.NotesServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NotesController {
    protected static final String NOTE_DELETED = "Note deleted";

    private final NotesService notesService;

    public NotesController(NotesServiceImpl notesService) {
        this.notesService = notesService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteResponse> create(@RequestBody NoteRequest request) {
        var response = notesService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteResponse> getById(@PathVariable Long id) {
        var response = notesService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NoteResponse>> getAll() {
        var response = notesService.getAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        notesService.deleteById(id);
        return ResponseEntity.ok(NOTE_DELETED);
    }
}
