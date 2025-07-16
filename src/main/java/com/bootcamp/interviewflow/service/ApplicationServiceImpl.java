package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.exception.ApplicationNotFoundException;
import com.bootcamp.interviewflow.exception.UserNotFoundException;
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
    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ApplicationNotFoundException("Application with id " + id + " not found");
        }
        applicationRepository.deleteById(id);
    }

    @Override
    public List<ApplicationListDTO> findAllByUserId(Long userId) {
        return List.of();
    }
}
