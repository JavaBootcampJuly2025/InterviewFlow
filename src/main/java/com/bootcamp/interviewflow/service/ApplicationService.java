package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.model.ApplicationStatus;

import java.util.List;

public interface ApplicationService {
    List<ApplicationListDTO> findAllByUserId(Long userId);

    ApplicationResponse create(CreateApplicationRequest dto, Long userId);

    List<ApplicationListDTO> findAll();

    void delete(Long id, Long userId);

    ApplicationResponse partialUpdate(Long id, Long userId, UpdateApplicationRequest dto);

    List<ApplicationListDTO> findAllByUserIdAndStatus(Long userId, ApplicationStatus status);
}