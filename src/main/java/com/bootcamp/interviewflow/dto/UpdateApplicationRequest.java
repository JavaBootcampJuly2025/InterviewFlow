package com.bootcamp.interviewflow.dto;

import com.bootcamp.interviewflow.model.ApplicationStatus;
import com.bootcamp.interviewflow.validation.AtLeastOneField;
import com.bootcamp.interviewflow.validation.ValidWebUrl;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@AtLeastOneField(message = "At least one field must be provided for update")
public class UpdateApplicationRequest {

    @Size(min = 1, max = 255, message = "Company name must be between 1 and 255 characters")
    private String companyName;

    @ValidWebUrl(message = "Company link must be a valid URL")
    private String companyLink;

    @Size(min = 1, max = 255, message = "Position must be between 1 and 255 characters")
    private String position;

    private ApplicationStatus status;
}