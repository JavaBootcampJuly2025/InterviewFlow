package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.NoteRequest;
import com.bootcamp.interviewflow.entity.Note;
import com.bootcamp.interviewflow.repository.NoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotesServiceTest {
    MockedStatic<LocalDateTime> localDateTimeMocked;

    @Mock
    NoteRepository repository;
    @InjectMocks
    NotesServiceImpl service;

    @AfterEach
    void tearDown() {
        localDateTimeMocked.close();
    }

    @Test
    void shouldReturnSavedNote() {
        localDateTimeMocked = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime now = LocalDateTime.of(2022, 10, 22, 10, 0);
        localDateTimeMocked.when(LocalDateTime::now).thenReturn(now);

        Note note = new Note(1L, "Test note", now, now);

        Note testNote = new Note("Test note");
        when(repository.save(testNote)).thenReturn(note);

        var actual = service.create(new NoteRequest("Test note"));

        assertEquals(note.getId(), actual.id());
        assertEquals(note.getContent(), actual.content());
        assertEquals(note.getCreatedAt(), actual.createdAt());
        assertEquals(note.getUpdatedAt(), actual.updatedAt());

        note.setId(null);
        verify(repository).save(note);
    }

    @Test
    void getById() {
    }

    @Test
    void getAll() {
    }

    @Test
    void deleteById() {
    }
}