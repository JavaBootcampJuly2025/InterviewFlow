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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final ApplicationRepository applicationRepository;
    private final ApplicationListMapper applicationListMapper;
    private final ApplicationMapper applicationMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService; // Add notification service

    @Override
    public ApplicationResponse create(CreateApplicationRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        Application app = Application.builder()
                .status(ApplicationStatus.valueOf(dto.getStatus()))
                .companyName(dto.getCompanyName())
                .companyLink(dto.getCompanyLink())
                .position(dto.getPosition())
                .applyDate(dto.getApplyDate() != null ? dto.getApplyDate() : LocalDateTime.now())
                .interviewDate(dto.getInterviewDate())
                .emailNotificationsEnabled(dto.getEmailNotificationsEnabled() != null ?
                        dto.getEmailNotificationsEnabled() : false)
                .user(user)
                .build();

        Application savedApp = applicationRepository.save(app);


        if (savedApp.getInterviewDate() != null &&
                Boolean.TRUE.equals(savedApp.getEmailNotificationsEnabled())) {
            notificationService.scheduleInterviewReminder(savedApp, user);
            log.info("Interview reminder scheduled for application {}", savedApp.getId());
        }

        return applicationMapper.toResponse(savedApp);
    }

    @Override
    public List<ApplicationListDTO> findAllByUserId(Long userId) {
        log.info("Fetching applications for user ID: {}", userId);
        List<Application> applications = applicationRepository.findAllByUserId(userId);
        log.info("Found {} applications for user ID: {}", applications.size(), userId);
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    public List<ApplicationListDTO> findAll() {
        log.info("Fetching all applications");
        List<Application> applications = applicationRepository.findAll();
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Application application = findAndValidateOwnership(id, userId);

        // Cancel any scheduled notifications before deleting
        notificationService.cancelNotificationsForApplication(application.getId());

        applicationRepository.delete(application);
        log.info("Application {} deleted and notifications cancelled", application.getId());
    }

    @Override
    @Transactional
    public ApplicationResponse partialUpdate(Long id, Long userId, UpdateApplicationRequest dto) {
        Application application = findAndValidateOwnership(id, userId);

        // Store original values to detect changes
        LocalDateTime originalInterviewDate = application.getInterviewDate();
        Boolean originalNotificationEnabled = application.getEmailNotificationsEnabled();

        Application updatedApp = applicationMapper.updateEntityFromDto(dto, application);
        Application savedApp = applicationRepository.save(updatedApp);

        // Handle notification scheduling based on changes
        handleNotificationScheduling(savedApp, originalInterviewDate, originalNotificationEnabled);

        log.info("Application {} updated", savedApp.getId());
        return applicationMapper.toResponse(savedApp);
    }

    private void handleNotificationScheduling(Application updatedApp,
                                              LocalDateTime originalInterviewDate,
                                              Boolean originalNotificationEnabled) {

        LocalDateTime newInterviewDate = updatedApp.getInterviewDate();
        Boolean newNotificationEnabled = updatedApp.getEmailNotificationsEnabled();

        // Check if interview date or notification settings changed
        boolean interviewDateChanged = (originalInterviewDate == null && newInterviewDate != null) ||
                (originalInterviewDate != null && !originalInterviewDate.equals(newInterviewDate)) ||
                (originalInterviewDate != null && newInterviewDate == null);

        boolean notificationSettingChanged = !Boolean.TRUE.equals(originalNotificationEnabled) &&
                Boolean.TRUE.equals(newNotificationEnabled) ||
                Boolean.TRUE.equals(originalNotificationEnabled) &&
                        !Boolean.TRUE.equals(newNotificationEnabled);

        if (interviewDateChanged || notificationSettingChanged) {
            // Cancel existing notifications first
            notificationService.cancelNotificationsForApplication(updatedApp.getId());

            // Schedule new notification if conditions are met
            if (newInterviewDate != null && Boolean.TRUE.equals(newNotificationEnabled)) {
                notificationService.scheduleInterviewReminder(updatedApp, updatedApp.getUser());
                log.info("Interview reminder rescheduled for application {}", updatedApp.getId());
            } else {
                log.info("Interview reminder cancelled for application {} - conditions not met", updatedApp.getId());
            }
        }
    }

    private Application findAndValidateOwnership(Long id, Long userId) {
        return applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application with id " + id + " not found"));
    }
}