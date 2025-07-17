package com.bootcamp.interviewflow.dto;

import lombok.Data;


@Data
public class ApplicationListResponse {
    private Long id;
    private String companyName;
    private String position;
    private String status;
}
