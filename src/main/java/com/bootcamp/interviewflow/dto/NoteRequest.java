package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to create or update a note for a job application")
public record NoteRequest(
        @NotNull
        @Schema(description = "ID of the associated job application", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        Long applicationId,

        @NotBlank(message = "Note cannot be empty")
        @Schema(description = "Content of the note", example = "Had a great conversation with the recruiter.", requiredMode = Schema.RequiredMode.REQUIRED)
        String content
) {
}
