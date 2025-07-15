package com.bootcamp.interviewflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NoteResponseDTO {
    private Long id;
    private String content;
    private Long applicationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}