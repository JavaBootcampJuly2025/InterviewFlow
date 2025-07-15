package com.bootcamp.interviewflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationResponseDTO {
    private Long id;
    private String status;
    private String companyName;
    private String companyLink;
    private String position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
}
