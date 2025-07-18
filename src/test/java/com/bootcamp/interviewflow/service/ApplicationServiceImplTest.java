package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.mapper.ApplicationListMapper;
import com.bootcamp.interviewflow.mapper.ApplicationMapper;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.bootcamp.interviewflow.model.ApplicationStatus.APPLIED;
import static com.bootcamp.interviewflow.model.ApplicationStatus.REJECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationListMapper applicationListMapper;

    @Mock
    private ApplicationMapper applicationMapper;

    @InjectMocks
    private ApplicationServiceImpl service;

    private final Long userId = 1L;
    private LocalDateTime now;
    private Application app1, app2;
    private List<Application> applications;
    private List<ApplicationListDTO> dtos;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        User user = new User();
        user.setId(userId);

        app1 = new Application();
        app1.setId(1L);
        app1.setStatus(APPLIED);
        app1.setCompanyName("Acme Corp");
        app1.setPosition("Software Engineer");
        app1.setCreatedAt(now);
        app1.setUpdatedAt(now);
        app1.setUser(user);

        app2 = new Application();
        app2.setId(2L);
        app2.setStatus(REJECTED);
        app2.setCompanyName("Globex");
        app2.setPosition("Business Analyst");
        app2.setCreatedAt(now);
        app2.setUpdatedAt(now);
        app2.setUser(user);

        applications = List.of(app1, app2);

        dtos = List.of(
                new ApplicationListDTO(1L, APPLIED, "Acme Corp", "https://acme.example", "Software Engineer", now, now),
                new ApplicationListDTO(2L, REJECTED, "Globex", "https://globex.example", "Business Analyst", now, now)
        );
    }

    @Test
    void findAllByUserId_ShouldReturnMappedDTOs() {
        when(applicationRepository.findAllByUserId(userId)).thenReturn(applications);
        when(applicationListMapper.toApplicationListDTOs(applications)).thenReturn(dtos);

        List<ApplicationListDTO> result = service.findAllByUserId(userId);

        assertThat(result).hasSize(2).isEqualTo(dtos);
        verify(applicationRepository).findAllByUserId(userId);
        verify(applicationListMapper).toApplicationListDTOs(applications);
    }

    @Test
    void findAllByUserId_ShouldReturnEmptyList_WhenNoApplications() {
        when(applicationRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(applicationListMapper.toApplicationListDTOs(List.of())).thenReturn(List.of());

        List<ApplicationListDTO> result = service.findAllByUserId(userId);

        assertThat(result).isEmpty();
        verify(applicationRepository).findAllByUserId(userId);
        verify(applicationListMapper).toApplicationListDTOs(List.of());
    }

    @Test
    void create_shouldSaveApplicationWithCorrectFields() {
        CreateApplicationRequest dto = new CreateApplicationRequest();
        dto.setCompanyName("TestCompany");
        dto.setCompanyLink("https://testcompany.com");
        dto.setPosition("Java Dev");
        dto.setStatus("APPLIED");
        dto.setUserId(userId);

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        Application testApp = new Application();
        testApp.setCompanyName(dto.getCompanyName());
        testApp.setCompanyLink(dto.getCompanyLink());
        testApp.setPosition(dto.getPosition());
        testApp.setStatus(ApplicationStatus.valueOf(dto.getStatus()));
        testApp.setUser(user);

        ApplicationResponse appResponse = new ApplicationResponse(
                testApp.getId(),
                testApp.getStatus(),
                testApp.getCompanyName(),
                testApp.getCompanyLink(),
                testApp.getPosition(),
                testApp.getCreatedAt(),
                testApp.getUpdatedAt());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(applicationRepository.save(testApp)).thenReturn(testApp);
        when(applicationMapper.toResponse(testApp)).thenReturn(appResponse);

        ApplicationResponse saved = service.create(dto);

        assertEquals("TestCompany", saved.companyName());
        assertEquals("https://testcompany.com", saved.companyLink());
        assertEquals("Java Dev", saved.position());
        assertEquals(ApplicationStatus.APPLIED, saved.status());

        verify(userRepository).findById(userId);
        verify(applicationRepository).save(any(Application.class));
        verify(applicationMapper).toResponse(testApp);
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

    @Test
    void partialUpdate_shouldReturnDtoWithUpdatedFields() {
        Long appId = 1L;
        UpdateApplicationRequest dto = new UpdateApplicationRequest();
        dto.setCompanyName("New Name");

        Application existing = new Application();
        existing.setId(appId);
        existing.setCompanyName("Old Name");
        existing.setCompanyLink("oldlink.com");
        existing.setPosition("Old Position");
        existing.setStatus(APPLIED);

        Application patched = new Application();
        patched.setId(appId);
        patched.setCompanyName("New Name");
        patched.setCompanyLink("oldlink.com");
        patched.setPosition("Old Position");
        patched.setStatus(APPLIED);

        ApplicationResponse respDto = new ApplicationResponse(
                appId,
                APPLIED,
                "New Name",
                "oldlink.com",
                "Old Position",
                null,
                null
        );

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(existing));
        when(applicationMapper.updateEntityFromDto(dto, existing)).thenReturn(patched);
        when(applicationRepository.save(patched)).thenReturn(patched);
        when(applicationMapper.toResponse(patched)).thenReturn(respDto);

        ApplicationResponse updated = service.partialUpdate(appId, dto);

        assertEquals("New Name", updated.companyName());
        assertEquals("oldlink.com", updated.companyLink());
        assertEquals("Old Position", updated.position());
        assertEquals(APPLIED, updated.status());

        verify(applicationRepository).findById(appId);
        verify(applicationMapper).updateEntityFromDto(dto, existing);
        verify(applicationRepository).save(patched);
        verify(applicationMapper).toResponse(patched);
    }

    @Test
    void partialUpdate_shouldReturnDtoWhenUpdatingMultipleFields() {
        Long appId = 2L;
        UpdateApplicationRequest dto = new UpdateApplicationRequest();
        dto.setCompanyName("New");
        dto.setPosition("NewPosition");
        dto.setStatus(ApplicationStatus.ACCEPTED);

        Application existing = new Application();
        existing.setId(appId);
        existing.setCompanyName("Old");
        existing.setCompanyLink("old.com");
        existing.setPosition("Old");
        existing.setStatus(APPLIED);

        Application patched = new Application();
        patched.setId(appId);
        patched.setCompanyName("New");
        patched.setCompanyLink("old.com");
        patched.setPosition("NewPosition");
        patched.setStatus(ApplicationStatus.ACCEPTED);

        ApplicationResponse respDto = new ApplicationResponse(
                appId,
                ApplicationStatus.ACCEPTED,
                "New",
                "old.com",
                "NewPosition",
                null,
                null
        );

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(existing));
        when(applicationMapper.updateEntityFromDto(dto, existing)).thenReturn(patched);
        when(applicationRepository.save(patched)).thenReturn(patched);
        when(applicationMapper.toResponse(patched)).thenReturn(respDto);

        ApplicationResponse updated = service.partialUpdate(appId, dto);

        assertEquals("New", updated.companyName());
        assertEquals("old.com", updated.companyLink());
        assertEquals("NewPosition", updated.position());
        assertEquals(ApplicationStatus.ACCEPTED, updated.status());

        verify(applicationMapper).updateEntityFromDto(dto, existing);
        verify(applicationRepository).save(patched);
        verify(applicationMapper).toResponse(patched);
    }

    @Test
    void partialUpdate_shouldThrowExceptionIfAppNotFound() {
        Long appId = 33L;
        UpdateApplicationRequest dto = new UpdateApplicationRequest();
        dto.setCompanyName("Whatever");

        when(applicationRepository.findById(appId)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> service.partialUpdate(appId, dto));

        verify(applicationRepository).findById(appId);
        verify(applicationRepository, never()).save(any());
        verifyNoInteractions(applicationMapper);
    }

    @Test
    void partialUpdate_shouldReturnDtoWhenDtoIsEmpty() {
        Long appId = 4L;
        UpdateApplicationRequest dto = new UpdateApplicationRequest();

        Application existing = new Application();
        existing.setId(appId);
        existing.setCompanyName("OldName");
        existing.setCompanyLink("oldlink.com");
        existing.setPosition("OldPosition");
        existing.setStatus(REJECTED);

        Application patched = new Application();
        patched.setId(appId);
        patched.setCompanyName("OldName");
        patched.setCompanyLink("oldlink.com");
        patched.setPosition("OldPosition");
        patched.setStatus(REJECTED);

        ApplicationResponse respDto = new ApplicationResponse(
                appId,
                REJECTED,
                "OldName",
                "oldlink.com",
                "OldPosition",
                null,
                null
        );

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(existing));
        when(applicationMapper.updateEntityFromDto(dto, existing)).thenReturn(patched);
        when(applicationRepository.save(patched)).thenReturn(patched);
        when(applicationMapper.toResponse(patched)).thenReturn(respDto);

        ApplicationResponse updated = service.partialUpdate(appId, dto);

        assertEquals("OldName", updated.companyName());
        assertEquals("oldlink.com", updated.companyLink());
        assertEquals("OldPosition", updated.position());
        assertEquals(REJECTED, updated.status());

        verify(applicationMapper).updateEntityFromDto(dto, existing);
        verify(applicationRepository).save(patched);
        verify(applicationMapper).toResponse(patched);
    }

}