package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Metadata about a user's uploaded file")
public record FileMetadataResponse(

        @Schema(description = "Unique file identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Original filename", example = "resume.pdf")
        String filename,

        @Schema(description = "MIME type of the file", example = "application/pdf")
        String contentType,

        @Schema(description = "Timestamp when the file was uploaded")
        LocalDateTime createdAt

) {
}
