package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response containing details about a file operation (e.g., upload, delete)")
public record FileResponse(
        @Schema(description = "Short description of the file operation result",
                example = "File uploaded successfully")
        String message,
        @Schema(description = "Unique identifier (UUID) of the stored file",
                example = "a81bcfda-eeeb-4d57-8b2d-72dd3b52fbd3")
        UUID fileId
) {
}
