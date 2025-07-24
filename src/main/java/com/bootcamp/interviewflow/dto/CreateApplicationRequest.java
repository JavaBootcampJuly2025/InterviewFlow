package com.bootcamp.interviewflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Schema(description = "Request to create a new job application")
public class CreateApplicationRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    @Schema(description = "Name of the company", example = "InterviewFlow", requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyName;

    @Size(max = 255, message = "Company link must not exceed 255 characters")
    @Schema(description = "Link to the company website", example = "https://interviewflow.com")
    private String companyLink;

    @NotBlank(message = "Position is required")
    @Size(max = 100, message = "Position must not exceed 100 characters")
    @Schema(description = "Job title or position applied for", example = "Software Engineer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String position;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    @Schema(description = "Job location", example = "New York, NY")
    private String location;

    @Schema(description = "Timestamp when you actually applied for the job (optional - defaults to creation time)",
            example = "2025-07-16 10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm[:ss]")
    private LocalDateTime applyDate;

    @Schema(description = "Timestamp when the interview is set for the application", example = "2025-07-16 10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm[:ss]")
    private LocalDateTime interviewDate;

    @Schema(description = "Enable reminder notifications for the interview?", example = "TRUE")
    private Boolean emailNotificationsEnabled;

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "APPLIED|HR_SCREEN|TECHNICAL_INTERVIEW|FINAL_INTERVIEW|OFFERED|ACCEPTED|REJECTED|WITHDRAWN",
            message = "Status must be one of: APPLIED, HR_SCREEN, TECHNICAL_INTERVIEW, FINAL_INTERVIEW, OFFERED, ACCEPTED, REJECTED, WITHDRAWN"
    )
    @Schema(
            description = "Status of the application",
            example = "APPLIED",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {
                    "APPLIED", "HR_SCREEN", "TECHNICAL_INTERVIEW",
                    "FINAL_INTERVIEW", "OFFERED", "ACCEPTED", "REJECTED", "WITHDRAWN"
            }
    )
    private String status;

    @Schema(description = "ID of the resume file to attach to this application")
    private String resumeId;
}