package com.bootcamp.interviewflow.dto;

import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Detailed response representing a job application")
public record ApplicationResponse(

        @Schema(description = "Unique ID of the application", example = "1001")
        Long id,

        @Schema(
                description = "Current status of the application",
                example = "APPLIED"
        )
        ApplicationStatus status,

        @Schema(description = "Name of the company", example = "InterviewFlow")
        String companyName,

        @Schema(description = "URL of the company website", example = "https://interviewflow.com")
        String companyLink,

        @Schema(description = "Position or title applied for", example = "Backend Engineer")
        String position,

        @Schema(description = "Timestamp when the application was created", example = "2025-07-18 14:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the application was last updated", example = "2025-07-18 15:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt

) {}
