package com.bootcamp.interviewflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoteRequest(
        @NotNull Long applicationId,
        @NotBlank(message = "Note cannot be empty") String content
) {
}
