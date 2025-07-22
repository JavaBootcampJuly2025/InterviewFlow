package com.bootcamp.interviewflow.dto;

import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Summary of a job application")
public record ApplicationListDTO(
        @Schema(description = "Application ID", example = "101")
        Long id,
        @Schema(description = "Current status of the application", example = "APPLIED")
        ApplicationStatus status,
        @Schema(description = "Name of the company", example = "InterviewFlow")
        String companyName,
        @Schema(description = "Link to the company's website", example = "https://interviewflow.com")
        String companyLink,
        @Schema(description = "Position applied for", example = "Backend Engineer")
        String position,

        @Schema(description = "Timestamp when you actually applied for the job (optional - defaults to creation time)",
                example = "2025-07-16T10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:[ss]")
        LocalDateTime applyDate,

        @Schema(description = "Timestamp when the application was created", example = "2025-07-17 10:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the interview is set for the application", example = "2025-07-18 14:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime interviewDate,

        @Schema(description = "Enable reminder notifications for the interview?", example = "TRUE")
        Boolean emailNotificationEnabled,

        @Schema(description = "Timestamp when the application was last updated", example = "2025-07-18 12:15:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
}