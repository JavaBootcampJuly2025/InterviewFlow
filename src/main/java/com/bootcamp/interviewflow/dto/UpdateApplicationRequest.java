package com.bootcamp.interviewflow.dto;

import com.bootcamp.interviewflow.model.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateApplicationRequest {
    private String companyName;
    private String companyLink;
    private String position;
    private ApplicationStatus status;
    private LocalDateTime applyDate;
}
