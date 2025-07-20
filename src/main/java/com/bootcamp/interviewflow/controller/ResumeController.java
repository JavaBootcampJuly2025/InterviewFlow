package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.ResumeResponse;
import com.bootcamp.interviewflow.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> uploadResume(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {

        ResumeResponse response = resumeService.storeResume(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

