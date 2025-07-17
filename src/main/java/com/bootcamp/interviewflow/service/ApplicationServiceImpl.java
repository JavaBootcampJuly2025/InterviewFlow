package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
import com.bootcamp.interviewflow.mapper.ApplicationListMapper;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
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
    private final UserRepository userRepository;

    @Override
    public Application create(CreateApplicationRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with id " + dto.getUserId() + " not found"));

        Application app = new Application();
        app.setCompanyName(dto.getCompanyName());
        app.setCompanyLink(dto.getCompanyLink());
        app.setPosition(dto.getPosition());
        app.setStatus(ApplicationStatus.valueOf(dto.getStatus()));
        app.setUser(user);

        return applicationRepository.save(app);
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
    public void delete(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ApplicationNotFoundException("Application with id " + id + " not found");
        }
        applicationRepository.deleteById(id);
    }

    @Override
    public Application partialUpdate(Long id, UpdateApplicationRequest dto) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Not found"));
        if (dto.getCompanyName() != null) app.setCompanyName(dto.getCompanyName());
        if (dto.getCompanyLink() != null) app.setCompanyLink(dto.getCompanyLink());
        if (dto.getPosition() != null) app.setPosition(dto.getPosition());
        if (dto.getStatus() != null) app.setStatus(dto.getStatus());
        return applicationRepository.save(app);
    }
}
