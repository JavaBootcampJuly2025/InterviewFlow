package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListResponse;
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
import org.springframework.data.domain.Sort;

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

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApplicationServiceImpl service;

    private final Long userId = 1L;
    private final Long applicationId = 1L;
    private LocalDateTime now;
    private Application app1, app2;
    private List<Application> applications;
    private List<ApplicationListResponse> dtos;
    private User user;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = User.builder()
                .id(userId)
                .build();

        app1 = Application.builder()
                .id(1L)
                .status(APPLIED)
                .companyName("Acme Corp")
                .position("Software Engineer")
                .createdAt(now)
                .updatedAt(now)
                .user(user)
                .build();

        app2 = Application.builder()
                .id(2L)
                .status(REJECTED)
                .companyName("Globex")
                .position("Business Analyst")
                .createdAt(now)
                .updatedAt(now)
                .user(user)
                .build();

        applications = List.of(app1, app2);

        dtos = List.of(
                new ApplicationListResponse(1L, APPLIED, "Acme Corp", "https://acme.example", "Software Engineer", "NY", now, now, now, true, now),
                new ApplicationListResponse(2L, REJECTED, "Globex", "https://globex.example", "Business Analyst","NY", now, now, now, true, now)
        );
    }

    @Test
    void findAllByUserId_ShouldReturnMappedDTOs() {
        when(applicationRepository.findAllByUserId(userId)).thenReturn(applications);
        when(applicationListMapper.toApplicationListDTOs(applications)).thenReturn(dtos);

        List<ApplicationListResponse> result = service.findAllByUserId(userId);

        assertThat(result).hasSize(2).isEqualTo(dtos);
        verify(applicationRepository).findAllByUserId(userId);
        verify(applicationListMapper).toApplicationListDTOs(applications);
    }

    @Test
    void findAllByUserId_ShouldReturnEmptyList_WhenNoApplications() {
        when(applicationRepository.findAllByUserId(userId)).thenReturn(List.of());
        when(applicationListMapper.toApplicationListDTOs(List.of())).thenReturn(List.of());

        List<ApplicationListResponse> result = service.findAllByUserId(userId);

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

        Application savedApp = Application.builder()
                .id(1L)
                .companyName(dto.getCompanyName())
                .companyLink(dto.getCompanyLink())
                .position(dto.getPosition())
                .status(ApplicationStatus.valueOf(dto.getStatus()))
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .build();

        ApplicationResponse appResponse = new ApplicationResponse(
                savedApp.getId(),
                savedApp.getStatus(),
                savedApp.getCompanyName(),
                savedApp.getCompanyLink(),
                savedApp.getPosition(),
                savedApp.getLocation(),
                savedApp.getApplyDate(),
                savedApp.getCreatedAt(),
                savedApp.getUpdatedAt(),
                savedApp.getInterviewDate(),
                savedApp.getEmailNotificationsEnabled());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(applicationRepository.save(any(Application.class))).thenReturn(savedApp);
        when(applicationMapper.toResponse(savedApp)).thenReturn(appResponse);

        ApplicationResponse saved = service.create(dto, userId);

        assertEquals("TestCompany", saved.companyName());
        assertEquals("https://testcompany.com", saved.companyLink());
        assertEquals("Java Dev", saved.position());
        assertEquals(ApplicationStatus.APPLIED, saved.status());

        verify(userRepository).findById(userId);
        verify(applicationRepository).save(any(Application.class));
        verify(applicationMapper).toResponse(savedApp);
    }

    @Test
    void create_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        CreateApplicationRequest dto = new CreateApplicationRequest();
        Long nonExistentUserId = 99L;

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.create(dto, nonExistentUserId)
        );
        assertEquals("User with id 99 not found", exception.getMessage());
        verify(userRepository).findById(nonExistentUserId);
        verifyNoInteractions(applicationRepository);
    }

    @Test
    void delete_shouldDeleteWhenApplicationExistsAndUserOwnsIt() {
        when(applicationRepository.findByIdAndUserId(applicationId, userId)).thenReturn(Optional.of(app1));

        service.delete(applicationId, userId);

        verify(applicationRepository).findByIdAndUserId(applicationId, userId);
        verify(notificationService).cancelNotificationsForApplication(applicationId);
        verify(applicationRepository).delete(app1);
    }

    @Test
    void delete_shouldThrowExceptionWhenApplicationNotFoundOrNotOwned() {
        Long nonExistentAppId = 42L;

        when(applicationRepository.findByIdAndUserId(nonExistentAppId, userId)).thenReturn(Optional.empty());

        ApplicationNotFoundException exception = assertThrows(
                ApplicationNotFoundException.class,
                () -> service.delete(nonExistentAppId, userId)
        );
        assertEquals("Application with id 42 not found", exception.getMessage());
        verify(applicationRepository).findByIdAndUserId(nonExistentAppId, userId);
        verify(applicationRepository, never()).deleteById(any());
    }

    @Test
    void partialUpdate_shouldReturnDtoWithUpdatedFields() {
        Long appId = 1L;
        UpdateApplicationRequest dto = new UpdateApplicationRequest();
        dto.setCompanyName("New Name");

        Application existing = Application.builder()
                .id(appId)
                .companyName("Old")
                .companyLink("old.com")
                .position("Old")
                .status(APPLIED)
                .build();

        Application patched = Application.builder()
                .id(appId)
                .companyName("New")
                .companyLink("old.com")
                .position("NewPosition")
                .status(ApplicationStatus.ACCEPTED)
                .build();

        ApplicationResponse respDto = new ApplicationResponse(
                appId,
                APPLIED,
                "New Name",
                "oldlink.com",
                "Old Position",
                "NY",
                null,
                null,
                null,
                null,
                true
        );

        when(applicationRepository.findByIdAndUserId(appId, userId)).thenReturn(Optional.of(existing));
        when(applicationMapper.updateEntityFromDto(dto, existing)).thenReturn(patched);
        when(applicationRepository.save(patched)).thenReturn(patched);
        when(applicationMapper.toResponse(patched)).thenReturn(respDto);

        ApplicationResponse updated = service.partialUpdate(appId, userId, dto);

        assertEquals("New Name", updated.companyName());
        assertEquals("oldlink.com", updated.companyLink());
        assertEquals("Old Position", updated.position());
        assertEquals(APPLIED, updated.status());

        verify(applicationRepository).findByIdAndUserId(appId, userId);
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

        Application existing = Application.builder()
                .id(appId)
                .companyName("Old")
                .companyLink("old.com")
                .position("Old")
                .status(APPLIED)
                .build();

        Application patched = Application.builder()
                .id(appId)
                .companyName("New")
                .companyLink("old.com")
                .position("NewPosition")
                .status(ApplicationStatus.ACCEPTED)
                .build();

        ApplicationResponse respDto = new ApplicationResponse(
                appId,
                ApplicationStatus.ACCEPTED,
                "New",
                "old.com",
                "NewPosition",
                "NY",
                null,
                null,
                null,
                null,
                true
        );

        when(applicationRepository.findByIdAndUserId(appId, userId)).thenReturn(Optional.of(existing));
        when(applicationMapper.updateEntityFromDto(dto, existing)).thenReturn(patched);
        when(applicationRepository.save(patched)).thenReturn(patched);
        when(applicationMapper.toResponse(patched)).thenReturn(respDto);

        ApplicationResponse updated = service.partialUpdate(appId, userId, dto);

        assertEquals("New", updated.companyName());
        assertEquals("old.com", updated.companyLink());
        assertEquals("NewPosition", updated.position());
        assertEquals(ApplicationStatus.ACCEPTED, updated.status());

        verify(applicationRepository).findByIdAndUserId(appId, userId);
        verify(applicationMapper).updateEntityFromDto(dto, existing);
        verify(applicationRepository).save(patched);
        verify(applicationMapper).toResponse(patched);
    }

    @Test
    void partialUpdate_shouldThrowExceptionIfAppNotFoundOrNotOwned() {
        Long appId = 33L;
        UpdateApplicationRequest dto = new UpdateApplicationRequest();
        dto.setCompanyName("Whatever");

        when(applicationRepository.findByIdAndUserId(appId, userId)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> service.partialUpdate(appId, userId, dto));

        verify(applicationRepository).findByIdAndUserId(appId, userId);
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
                "NY",
                null,
                null,
                null,
                null,
                true
        );

        when(applicationRepository.findByIdAndUserId(appId, userId)).thenReturn(Optional.of(existing));
        when(applicationMapper.updateEntityFromDto(dto, existing)).thenReturn(patched);
        when(applicationRepository.save(patched)).thenReturn(patched);
        when(applicationMapper.toResponse(patched)).thenReturn(respDto);

        ApplicationResponse updated = service.partialUpdate(appId, userId, dto);

        assertEquals("OldName", updated.companyName());
        assertEquals("oldlink.com", updated.companyLink());
        assertEquals("OldPosition", updated.position());
        assertEquals(REJECTED, updated.status());

        verify(applicationRepository).findByIdAndUserId(appId, userId);
        verify(applicationMapper).updateEntityFromDto(dto, existing);
        verify(applicationRepository).save(patched);
        verify(applicationMapper).toResponse(patched);
    }

    @Test
    void findAllByUserIdSorted_ShouldReturnAllApplicationsSorted() {
        Sort sort = Sort.by(Sort.Direction.DESC, "applyDate");

        when(applicationRepository.findAllByUserId(userId, sort)).thenReturn(applications);
        when(applicationListMapper.toApplicationListDTOs(applications)).thenReturn(dtos);

        List<ApplicationListResponse> result = service.findAllByUserIdSorted(userId, sort);

        assertThat(result).hasSize(2).isEqualTo(dtos);
        verify(applicationRepository).findAllByUserId(userId, sort);
        verify(applicationListMapper).toApplicationListDTOs(applications);
    }

    @Test
    void findAllByUserIdSorted_ShouldReturnEmptyList_WhenNoApplications() {
        Sort sort = Sort.by(Sort.Direction.ASC, "companyName");

        when(applicationRepository.findAllByUserId(userId, sort)).thenReturn(List.of());
        when(applicationListMapper.toApplicationListDTOs(List.of())).thenReturn(List.of());

        List<ApplicationListResponse> result = service.findAllByUserIdSorted(userId, sort);

        assertThat(result).isEmpty();
        verify(applicationRepository).findAllByUserId(userId, sort);
        verify(applicationListMapper).toApplicationListDTOs(List.of());
    }

    @Test
    void findAllByUserIdAndStatus_ShouldReturnEmptyList_WhenNoMatches() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        when(applicationRepository.findAllByUserIdAndStatus(userId, ApplicationStatus.WITHDRAWN, sort)).thenReturn(List.of());
        when(applicationListMapper.toApplicationListDTOs(List.of())).thenReturn(List.of());

        List<ApplicationListResponse> result = service.findAllByUserIdAndStatus(userId, ApplicationStatus.WITHDRAWN, sort);

        assertThat(result).isEmpty();
        verify(applicationRepository).findAllByUserIdAndStatus(userId, ApplicationStatus.WITHDRAWN, sort);
        verify(applicationListMapper).toApplicationListDTOs(List.of());
    }

    @Test
    void findAllByUserIdAndStatus_ShouldReturnSortedDTOs() {
        Sort sort = Sort.by(Sort.Direction.ASC, "companyName");

        List<Application> filteredApps = List.of(app1);
        List<ApplicationListResponse> mappedDTOs = List.of(dtos.get(0));

        when(applicationRepository.findAllByUserIdAndStatus(userId, APPLIED, sort)).thenReturn(filteredApps);
        when(applicationListMapper.toApplicationListDTOs(filteredApps)).thenReturn(mappedDTOs);

        List<ApplicationListResponse> result = service.findAllByUserIdAndStatus(userId, APPLIED, sort);

        assertThat(result).hasSize(1).isEqualTo(mappedDTOs);
        verify(applicationRepository).findAllByUserIdAndStatus(userId, APPLIED, sort);
        verify(applicationListMapper).toApplicationListDTOs(filteredApps);
    }
}