package com.bootcamp.interviewflow.dto;

import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        Long applicationId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
