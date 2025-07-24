package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListResponse;
import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ApplicationService {
    List<ApplicationListResponse> findAllByUserId(Long userId);

    ApplicationResponse create(CreateApplicationRequest dto, Long userId);

    List<ApplicationListResponse> findAll();

    void delete(Long id, Long userId);

    ApplicationResponse partialUpdate(Long id, Long userId, UpdateApplicationRequest dto);

    List<ApplicationListResponse> findAllByUserIdAndStatus(Long userId, ApplicationStatus status, Sort sort);

    List<ApplicationListResponse> findAllByUserIdSorted(Long userId, Sort sort);

}