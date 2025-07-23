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
import org.springframework.data.domain.Sort;
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

        return applicationMapper.toResponse(applicationRepository.save(app));
    }

    @Override
    public List<ApplicationListDTO> findAllByUserId(Long userId) {
        log.info("Fetching applications for user ID: {}", userId);
        List<Application> applications = applicationRepository.findAllByUserId(userId);
        log.info("Found {} applications for user ID: {}", applications.size(), userId);
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    public List<ApplicationListDTO> findAllByUserIdAndStatus(Long userId, ApplicationStatus status, Sort sort) {
        log.info("Fetching applications for user ID: {} with status: {}, sort: {}", userId, status, sort);
        List<Application> applications = applicationRepository.findAllByUserIdAndStatus(userId, status, sort);
        log.info("Found {} applications for user ID: {} with status: {}, sort: {}", applications.size(), userId, status, sort);
        return applicationListMapper.toApplicationListDTOs(applications);
    }

    @Override
    public List<ApplicationListDTO> findAllByUserIdSorted(Long userId, Sort sort) {
        log.info("Fetching sorted applications for user ID: {}, sort: {}", userId, sort);
        List<Application> applications = applicationRepository.findAllByUserId(userId, sort);
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
        findAndValidateOwnership(id, userId);
        applicationRepository.deleteById(id);
    }

    @Override
    public ApplicationResponse partialUpdate(Long id, Long userId, UpdateApplicationRequest dto) {
        Application application = findAndValidateOwnership(id, userId);
        Application updatedApp = applicationMapper.updateEntityFromDto(dto, application);

        log.info("Application partially updated: {}", updatedApp);
        return applicationMapper.toResponse(applicationRepository.save(updatedApp));
    }

    private Application findAndValidateOwnership(Long id, Long userId) {
        return applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application with id " + id + " not found"));
    }

}
