package com.bootcamp.interviewflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateApplicationDTO {
    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "APPLIED|HR_SCREEN|TECHNICAL_INTERVIEW|FINAL_INTERVIEW|OFFERED|ACCEPTED|REJECTED|WITHDRAWN",
            message = "Invalid application status"
    )
    private String status;

    private String companyName;
    private String companyLink;
    private String position;
}