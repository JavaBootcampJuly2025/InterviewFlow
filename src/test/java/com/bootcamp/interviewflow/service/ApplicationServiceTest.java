package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.CreateApplicationDTO;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Test
    void createFromDto_shouldSaveApplicationWithCorrectFields() {
        ApplicationRepository applicationRepository = mock(ApplicationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        ApplicationService service = new ApplicationService(applicationRepository, userRepository);

        CreateApplicationDTO dto = new CreateApplicationDTO();
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

        Application app = service.createFromDto(dto);

        assertEquals("TestCompany", app.getCompanyName());
        assertEquals("https://testcompany.com", app.getCompanyLink());
        assertEquals("Java Dev", app.getPosition());
        assertEquals(ApplicationStatus.APPLIED, app.getStatus());
        assertEquals(user, app.getUser());

        verify(userRepository).findById(1L);
        verify(applicationRepository).save(any(Application.class));
    }
}