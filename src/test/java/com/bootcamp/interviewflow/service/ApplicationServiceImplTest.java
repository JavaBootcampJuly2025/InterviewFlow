package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldSaveApplicationWithCorrectFields() {

        CreateApplicationRequest dto = new CreateApplicationRequest();
        dto.setCompanyName("TestCompany");
        dto.setCompanyLink("https://testcompany.com");
        dto.setPosition("Java Dev");
        dto.setStatus("APPLIED");
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

        Application app = service.create(dto);

        assertEquals("TestCompany", app.getCompanyName());
        assertEquals("https://testcompany.com", app.getCompanyLink());
        assertEquals("Java Dev", app.getPosition());
        assertEquals(ApplicationStatus.APPLIED, app.getStatus());
        assertEquals(user, app.getUser());

        verify(userRepository).findById(1L);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void create_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        CreateApplicationRequest dto = new CreateApplicationRequest();
        dto.setUserId(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.create(dto)
        );
        assertEquals("User with id 99 not found", exception.getMessage());
        verify(userRepository).findById(99L);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void delete_shouldDeleteWhenApplicationExists() {
        Long applicationId = 1L;

        when(applicationRepository.existsById(applicationId)).thenReturn(true);

        service.delete(applicationId);

        verify(applicationRepository).existsById(applicationId);
        verify(applicationRepository).deleteById(applicationId);
    }

    @Test
    void delete_shouldThrowExceptionWhenApplicationNotExists() {
        Long applicationId = 42L;

        when(applicationRepository.existsById(applicationId)).thenReturn(false);

        ApplicationNotFoundException exception = assertThrows(
                ApplicationNotFoundException.class,
                () -> service.delete(applicationId)
        );
        assertEquals("Application with id 42 not found", exception.getMessage());
        verify(applicationRepository).existsById(applicationId);

        verify(applicationRepository).existsById(applicationId);
    }
}