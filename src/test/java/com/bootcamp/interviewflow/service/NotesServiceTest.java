package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.NoteRequest;
import com.bootcamp.interviewflow.dto.NoteResponse;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.exception.NoteNotFoundException;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.model.Note;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotesServiceTest {
    MockedStatic<LocalDateTime> localDateTimeMocked;

    @Mock
    NoteRepository noteRepository;
    @Mock
    ApplicationRepository applicationRepository;
    @InjectMocks
    NotesServiceImpl service;

    @Test
    void shouldReturnCorrectNoteResponseWhenValidRequestProvided() {
        localDateTimeMocked = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime now = LocalDateTime.of(2022, 10, 22, 10, 0);
        localDateTimeMocked.when(LocalDateTime::now).thenReturn(now);

        var application = new Application(100L,
                ApplicationStatus.APPLIED,
                "TestCompany",
                "Link",
                "TestPosition",
                now,
                now,
                new User());
        Note note = new Note(1L, "Test note", application, now, now);
        Note testNote = new Note(application, "Test note");

        when(noteRepository.save(testNote)).thenReturn(note);
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));

        var actual = service.create(new NoteRequest(100L, "Test note"));

        assertEquals(note.getId(), actual.id());
        assertEquals(note.getContent(), actual.content());
        assertEquals(note.getApplication(), application);
        assertEquals(note.getCreatedAt(), actual.createdAt());
        assertEquals(note.getUpdatedAt(), actual.updatedAt());

        note.setId(null);
        verify(noteRepository, times(1)).save(testNote);
        verify(applicationRepository, times(1)).findById(100L);

        localDateTimeMocked.close();
    }

    @Test
    void shouldThrowApplicationNotFoundExceptionWhenNotValidRequestProvided() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.empty());

        var thrown = assertThrows(ApplicationNotFoundException.class,
                () -> service.create(new NoteRequest(100L, "Test note")));
        assertTrue(thrown.getMessage().contains("Application with Id 100 not found"));

        verify(applicationRepository, times(1)).findById(100L);
    }

    @Test
    void shouldReturnCorrectNoteResponseWhenValidIdProvided() {
        localDateTimeMocked = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime now = LocalDateTime.of(2022, 10, 22, 10, 0);
        localDateTimeMocked.when(LocalDateTime::now).thenReturn(now);

        var application = new Application(1L,
                ApplicationStatus.APPLIED,
                "TestCompany",
                "Link",
                "TestPosition",
                now,
                now,
                new User());
        Note note = new Note(100L, "Test note", application, now, now);

        when(noteRepository.findById(100L)).thenReturn(Optional.of(note));

        var actual = service.getById(100L);

        assertEquals(note.getId(), actual.id());
        assertEquals(note.getContent(), actual.content());
        assertEquals(note.getApplication(), application);
        assertEquals(note.getCreatedAt(), actual.createdAt());
        assertEquals(note.getUpdatedAt(), actual.updatedAt());

        verify(noteRepository, times(1)).findById(100L);

        localDateTimeMocked.close();
    }

    @Test
    void shouldThrowNoteNotFoundExceptionWhenNotValidRequestProvided() {
        when(noteRepository.findById(100L)).thenReturn(Optional.empty());

        var thrown = assertThrows(NoteNotFoundException.class,
                () -> service.getById(100L));
        assertTrue(thrown.getMessage().contains("Note with Id 100 not found"));

        verify(noteRepository, times(1)).findById(100L);
    }

    @Test
    void shouldReturnListOfNoteResponseWhenCalledGetAllByApplicationId() {
        localDateTimeMocked = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
        LocalDateTime now = LocalDateTime.of(2022, 10, 22, 10, 0);
        localDateTimeMocked.when(LocalDateTime::now).thenReturn(now);

        var application = new Application(1L,
                ApplicationStatus.APPLIED,
                "TestCompany",
                "Link",
                "TestPosition",
                now,
                now,
                new User());
        Note noteOne = new Note(100L, "Test note one", application, now, now);
        Note noteTwo = new Note(100L, "Test note two", application, now, now);
        Note noteThree = new Note(100L, "Test note three", application, now, now);
        List<Note> notes = List.of(noteOne, noteTwo, noteThree);

        var responseOne = new NoteResponse(
                noteOne.getId(),
                noteOne.getApplication().getId(),
                noteOne.getContent(),
                noteOne.getCreatedAt(),
                noteOne.getUpdatedAt());

        var responseTwo = new NoteResponse(
                noteTwo.getId(),
                noteTwo.getApplication().getId(),
                noteTwo.getContent(),
                noteTwo.getCreatedAt(),
                noteTwo.getUpdatedAt());

        var responseThree = new NoteResponse(
                noteThree.getId(),
                noteThree.getApplication().getId(),
                noteThree.getContent(),
                noteThree.getCreatedAt(),
                noteThree.getUpdatedAt());

        when(noteRepository.findAllByApplication_Id(1L)).thenReturn(notes);

        var actual = service.getAllByApplicationId(1L);

        assertTrue(actual.contains(responseOne));
        assertTrue(actual.contains(responseTwo));
        assertTrue(actual.contains(responseThree));

        verify(noteRepository, times(1)).findAllByApplication_Id(1L);

        localDateTimeMocked.close();
    }

    @Test
    void shouldDeleteNoteWhenValidIdProvided() {
        service.deleteById(100L);
        verify(noteRepository, times(1)).deleteById(100L);
    }

    @Test
    void shouldThrowNoteNotFoundExceptionWhenNotValidIdProvided() {
        doThrow(new EmptyResultDataAccessException(1)).when(noteRepository).deleteById(123L);

        var thrown = assertThrows(NoteNotFoundException.class,
                () -> service.deleteById(123L)
        );

        assertTrue(thrown.getMessage().contains("Note with Id 123 not found"));

        verify(noteRepository, times(1)).deleteById(123L);
    }
}