package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;

import java.util.List;

public interface ApplicationService {

    List<ApplicationListDTO> findAllByUserId(Long userId);

    ApplicationResponse create(CreateApplicationRequest dto);

    List<ApplicationListDTO> findAll(String status);

    void delete(Long id);

    ApplicationResponse partialUpdate(Long id, UpdateApplicationRequest dto);
}