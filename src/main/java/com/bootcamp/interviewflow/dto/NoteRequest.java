package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request to create or update a note for a job application")
public record NoteRequest(
        @NotNull
        @Schema(description = "ID of the associated job application", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        Long applicationId,

        @Schema(description = "Title of the note", example = "Call with recruiter")
        String title,

        @NotBlank(message = "Note cannot be empty")
        @Size(max = 2000, message = "Note content cannot exceed 2000 characters")
        @Schema(description = "Content of the note", example = "Had a great conversation with the recruiter.", requiredMode = Schema.RequiredMode.REQUIRED)
        String content,

        @Schema(description = "Tags for the note", example = "[\"urgent\",\"followup\"]")
        List<String> tags
) {
}
