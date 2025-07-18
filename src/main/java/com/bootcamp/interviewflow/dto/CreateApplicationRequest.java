package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user applying for the job", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

}