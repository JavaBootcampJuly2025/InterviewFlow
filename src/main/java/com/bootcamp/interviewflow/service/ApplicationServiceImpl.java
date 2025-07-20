package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.exception.ResumeNotFoundException;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.mapper.ApplicationListMapper;
import com.bootcamp.interviewflow.mapper.ApplicationMapper;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.ResumeRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final ApplicationRepository applicationRepository;
    private final ApplicationListMapper applicationListMapper;
    private final ApplicationMapper applicationMapper;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    @Override
    public ApplicationResponse create(CreateApplicationRequest dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));

        Application app = new Application();
        app.setCompanyName(dto.getCompanyName());
        app.setCompanyLink(dto.getCompanyLink());
        app.setPosition(dto.getPosition());
        app.setApplyDate(dto.getApplyDate());
        app.setStatus(ApplicationStatus.valueOf(dto.getStatus()));
        app.setUser(user);

        if (dto.getResumeId() != null) {
            var resume = resumeRepository.findById(dto.getResumeId())
                    .orElseThrow(() -> new ResumeNotFoundException("Resume not found with id " + dto.getResumeId()));
            app.setResume(resume);
            log.info("Attached resume {} to new application for user {}", resume.getId(), user.getId());
        }
        Application saved = applicationRepository.save(app);
        log.info("Created application {} for user {} (company: {}, position: {})",
                saved.getId(), user.getId(), saved.getCompanyName(), saved.getPosition());
        return applicationMapper.toResponse(saved);
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
        findAndValidateOwnership(id, userId);
        applicationRepository.deleteById(id);
    }

    @Override
    public ApplicationResponse partialUpdate(Long id, Long userId, UpdateApplicationRequest dto) {
        Application application = findAndValidateOwnership(id, userId);
        Application updatedApp = applicationMapper.updateEntityFromDto(dto, application);

        if (dto.getResumeId() != null && resumeRepository != null) {
            var resume = resumeRepository.findById(dto.getResumeId())
                    .orElseThrow(() -> new ResumeNotFoundException("Resume not found with id " + dto.getResumeId()));
            updatedApp.setResume(resume);
            log.info("Attached resume {} to application {}", resume.getId(), id);
        } else if (dto.getResumeId() == null && resumeRepository != null) {
            updatedApp.setResume(null);
            log.info("Detached resume from application {}", id);
        }

        log.info("Application partially updated: {}", updatedApp);
        return applicationMapper.toResponse(applicationRepository.save(updatedApp));
    }

    private Application findAndValidateOwnership(Long id, Long userId) {
        return applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application with id " + id + " not found"));
    }

}
