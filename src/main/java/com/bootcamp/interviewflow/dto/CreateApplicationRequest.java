package com.bootcamp.interviewflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateApplicationRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String companyName;

    @Size(max = 255, message = "Company link must not exceed 255 characters")
    private String companyLink;

    @NotBlank(message = "Position is required")
    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "APPLIED|HR_SCREEN|TECHNICAL_INTERVIEW|FINAL_INTERVIEW|OFFERED|ACCEPTED|REJECTED|WITHDRAWN",
            message = "Status must be one of: APPLIED, HR_SCREEN, TECHNICAL_INTERVIEW, FINAL_INTERVIEW, OFFERED, ACCEPTED, REJECTED, WITHDRAWN"
    )
    private String status;

    private Long userId;

}