package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.ResumeResponse;
import com.bootcamp.interviewflow.security.UserPrincipal;
import com.bootcamp.interviewflow.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resumes", description = "Upload resume files")
public class ResumeController {

    private final ResumeService resumeService;

    @Operation(summary = "Upload a resume (PDF only, max 5MB)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resume uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> uploadResume(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file) {

        Long userId = userPrincipal.getId();
        ResumeResponse response = resumeService.uploadResume(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
