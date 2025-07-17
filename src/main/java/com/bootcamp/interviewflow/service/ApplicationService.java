package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListResponse;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.model.Application;

import java.util.List;

public interface ApplicationService {

    Application create(CreateApplicationRequest dto);

    List<Application> findAll();

    void delete(Long id);

    List<ApplicationListResponse> findAllByUserId(Long userId);
}