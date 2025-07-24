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
import com.bootcamp.interviewflow.model.Resume;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final ApplicationRepository applicationRepository;
    private final ApplicationListMapper applicationListMapper;
    private final ApplicationMapper applicationMapper;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final NotificationService notificationService;

    @Override
    public ApplicationResponse create(CreateApplicationRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        Resume resume = null;
        if (dto.getResumeId() != null && !dto.getResumeId().trim().isEmpty()) {
            try {
                UUID resumeUuid = UUID.fromString(dto.getResumeId());
                resume = resumeRepository.findByIdAndUserId(resumeUuid, userId)
                        .orElseThrow(() -> new IllegalArgumentException("Resume not found or does not belong to user"));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid resume ID provided: {}", dto.getResumeId());
                throw new IllegalArgumentException("Invalid resume ID format");
            }
        }

        log.info("Creating application for user ID: {}", userId);
        Application app = Application.builder()
                .status(ApplicationStatus.valueOf(dto.getStatus()))
                .companyName(dto.getCompanyName())
                .companyLink(dto.getCompanyLink())
                .position(dto.getPosition())
                .location(dto.getLocation())
                .applyDate(dto.getApplyDate() != null ? dto.getApplyDate() : LocalDateTime.now())
                .interviewDate(dto.getInterviewDate())
                .emailNotificationsEnabled(dto.getEmailNotificationsEnabled() != null &&
                        dto.getEmailNotificationsEnabled())
                .user(user)
                .resume(resume)
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
    public List<ApplicationListResponse> findAllByUserId(Long userId) {
        log.info("Fetching applications for user ID: {}", userId);
        List<Application> applications = applicationRepository.findAllByUserId(userId);
        log.info("Found {} applications for user ID: {}", applications.size(), userId);
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    public List<ApplicationListResponse> findAllByUserIdAndStatus(Long userId, ApplicationStatus status, Sort sort) {
        log.info("Fetching applications for user ID: {} with status: {}, sort: {}", userId, status, sort);
        List<Application> applications = applicationRepository.findAllByUserIdAndStatus(userId, status, sort);
        log.info("Found {} applications for user ID: {} with status: {}, sort: {}", applications.size(), userId, status, sort);
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    public List<ApplicationListResponse> findAllByUserIdSorted(Long userId, Sort sort) {
        log.info("Fetching sorted applications for user ID: {}, sort: {}", userId, sort);
        List<Application> applications = applicationRepository.findAllByUserId(userId, sort);
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    public List<ApplicationListResponse> findAll() {
        log.info("Fetching applications:");
        List<Application> applications = applicationRepository.findAll();
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Application application = findAndValidateOwnership(id, userId);

        notificationService.cancelNotificationsForApplication(application.getId());

        applicationRepository.delete(application);
        log.info("Application {} deleted and notifications cancelled", application.getId());
    }

    @Override
    @Transactional
    public ApplicationResponse partialUpdate(Long id, Long userId, UpdateApplicationRequest dto) {
        Application application = findAndValidateOwnership(id, userId);

        LocalDateTime originalInterviewDate = application.getInterviewDate();
        Boolean originalNotificationEnabled = application.getEmailNotificationsEnabled();

        Application updatedApp = applicationMapper.updateEntityFromDto(dto, application);
        Application savedApp = applicationRepository.save(updatedApp);

        handleNotificationScheduling(savedApp, originalInterviewDate, originalNotificationEnabled);

        log.info("Application {} updated", savedApp.getId());
        return applicationMapper.toResponse(savedApp);
    }

    private void handleNotificationScheduling(Application updatedApp,
                                              LocalDateTime originalInterviewDate,
                                              Boolean originalNotificationEnabled) {

        LocalDateTime newInterviewDate = updatedApp.getInterviewDate();
        Boolean newNotificationEnabled = updatedApp.getEmailNotificationsEnabled();

        boolean interviewDateChanged = (originalInterviewDate == null && newInterviewDate != null) ||
                (originalInterviewDate != null && !originalInterviewDate.equals(newInterviewDate)) ||
                (originalInterviewDate != null && newInterviewDate == null);

        boolean notificationSettingChanged = !Boolean.TRUE.equals(originalNotificationEnabled) &&
                Boolean.TRUE.equals(newNotificationEnabled) ||
                Boolean.TRUE.equals(originalNotificationEnabled) &&
                        !Boolean.TRUE.equals(newNotificationEnabled);

        if (interviewDateChanged || notificationSettingChanged) {

            notificationService.cancelNotificationsForApplication(updatedApp.getId());

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