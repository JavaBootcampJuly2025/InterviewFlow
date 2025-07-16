package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.NoteRequest;
import com.bootcamp.interviewflow.dto.NoteResponse;

import java.util.List;

public interface NotesService {
    NoteResponse create(NoteRequest request);
    NoteResponse getById(Long id);
    List<NoteResponse> getAllByApplicationId(Long applicationId);
    void deleteById(Long id);
}
