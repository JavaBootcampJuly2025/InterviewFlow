package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ApplicationListDTO;
import com.bootcamp.interviewflow.dto.ApplicationResponse;
import com.bootcamp.interviewflow.dto.CreateApplicationRequest;
import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.model.Application;

import java.util.List;

public interface ApplicationService {

    List<ApplicationListDTO> findAllByUserId(Long userId);

    Application create(CreateApplicationRequest dto);

    List<ApplicationListDTO> findAll();

    void delete(Long id);

    ApplicationResponse partialUpdate(Long id, UpdateApplicationRequest dto);
}