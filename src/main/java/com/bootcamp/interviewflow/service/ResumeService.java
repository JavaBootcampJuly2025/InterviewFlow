package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ResumeResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {
    ResumeResponse uploadResume(Long userId, MultipartFile file);
}
