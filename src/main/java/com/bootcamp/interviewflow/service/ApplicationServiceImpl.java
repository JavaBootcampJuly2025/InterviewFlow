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
    private final EmailReminderService emailReminderService; // Add email service

    @Override
    public ApplicationResponse create(CreateApplicationRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        Application app = new Application();
        app.setCompanyName(dto.getCompanyName());
        app.setCompanyLink(dto.getCompanyLink());
        app.setPosition(dto.getPosition());
        app.setApplyDate(dto.getApplyDate() != null ? dto.getApplyDate() : LocalDateTime.now());
        app.setInterviewDate(dto.getInterviewDate());
        app.setEmailNotificationsEnabled(dto.getEmailNotificationsEnabled() != null ?
                dto.getEmailNotificationsEnabled() : false);
        app.setStatus(ApplicationStatus.valueOf(dto.getStatus()));
        app.setUser(user);

        Application savedApp = applicationRepository.save(app);

        // Schedule interview reminder if conditions are met
        if (savedApp.getInterviewDate() != null &&
                Boolean.TRUE.equals(savedApp.getEmailNotificationsEnabled())) {
            emailReminderService.scheduleInterviewReminder(savedApp, user.getEmail());
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
        log.info("Fetching applications:");
        List<Application> applications = applicationRepository.findAll();
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    public void delete(Long id, Long userId) {
        Application application = findAndValidateOwnership(id, userId);

        // Cancel any scheduled reminders before deleting
        emailReminderService.cancelInterviewReminder(id);

        applicationRepository.deleteById(id);
        log.info("Application {} deleted and reminders cancelled", id);
    }

    @Override
    public ApplicationResponse partialUpdate(Long id, Long userId, UpdateApplicationRequest dto) {
        Application application = findAndValidateOwnership(id, userId);

        // Store original interview date and notification settings to detect changes
        LocalDateTime originalInterviewDate = application.getInterviewDate();
        Boolean originalNotificationEnabled = application.getEmailNotificationsEnabled();

        Application updatedApp = applicationMapper.updateEntityFromDto(dto, application);
        Application savedApp = applicationRepository.save(updatedApp);

        // Handle reminder scheduling based on changes
        handleReminderScheduling(savedApp, originalInterviewDate, originalNotificationEnabled);

        log.info("Application partially updated: {}", savedApp);
        return applicationMapper.toResponse(savedApp);
    }

    private void handleReminderScheduling(Application updatedApp,
                                          LocalDateTime originalInterviewDate,
                                          Boolean originalNotificationEnabled) {

        LocalDateTime newInterviewDate = updatedApp.getInterviewDate();
        Boolean newNotificationEnabled = updatedApp.getEmailNotificationsEnabled();

        // Check if interview date or notification settings changed
        boolean interviewDateChanged = (originalInterviewDate == null && newInterviewDate != null) ||
                (originalInterviewDate != null && !originalInterviewDate.equals(newInterviewDate));

        boolean notificationSettingChanged = !Boolean.TRUE.equals(originalNotificationEnabled) &&
                Boolean.TRUE.equals(newNotificationEnabled);

        if (interviewDateChanged || notificationSettingChanged) {
            // Cancel existing reminder first
            emailReminderService.cancelInterviewReminder(updatedApp.getId());

            // Schedule new reminder if conditions are met
            if (newInterviewDate != null && Boolean.TRUE.equals(newNotificationEnabled)) {
                emailReminderService.scheduleInterviewReminder(updatedApp, updatedApp.getUser().getEmail());
                log.info("Interview reminder rescheduled for application {}", updatedApp.getId());
            }
        } else if (Boolean.FALSE.equals(newNotificationEnabled)) {
            // If notifications are disabled, cancel any existing reminders
            emailReminderService.cancelInterviewReminder(updatedApp.getId());
            log.info("Interview reminder cancelled for application {} - notifications disabled", updatedApp.getId());
        }
    }

    private Application findAndValidateOwnership(Long id, Long userId) {
        return applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application with id " + id + " not found"));
    }
}