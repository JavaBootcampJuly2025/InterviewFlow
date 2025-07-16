package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.model.Application;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.ApplicationRepository;
import com.bootcamp.interviewflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public Application create(CreateApplicationRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application app = new Application();
        app.setCompanyName(dto.getCompanyName());
        app.setCompanyLink(dto.getCompanyLink());
        app.setPosition(dto.getPosition());
        app.setStatus(ApplicationStatus.valueOf(dto.getStatus()));
        app.setUser(user);

        return applicationRepository.save(app);
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    public void delete(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ApplicationNotFoundException("Application with id " + id + " not found");
        }
        applicationRepository.deleteById(id);
    }
}
