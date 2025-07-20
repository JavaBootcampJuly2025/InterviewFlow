package com.bootcamp.interviewflow.dto;

import com.bootcamp.interviewflow.model.ApplicationStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateApplicationRequest {

    @Size(min = 1, max = 255, message = "Company name must be between 1 and 255 characters")
    private String companyName;

    @Pattern(
            regexp = "^(https?://)(localhost(:\\d+)?|([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}(:\\d+)?)(/[^?#]*)?(\\?[^#]*)?(#.*)?$",
            message = "Company link must be a valid URL"
    )
    private String companyLink;

    @Size(min = 1, max = 255, message = "Position must be between 1 and 255 characters")
    private String position;

    private ApplicationStatus status;
}