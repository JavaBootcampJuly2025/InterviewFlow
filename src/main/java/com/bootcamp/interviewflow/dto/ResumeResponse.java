package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response with details about the uploaded resume")
public record ResumeResponse(
        @Schema(description = "Unique identifier (UUID) of the stored file",
                example = "a81bcfda-eeeb-4d57-8b2d-72dd3b52fbd3")
        UUID fileId,

        @Schema(description = "Original filename of the uploaded resume", example = "cv_alex_spring.pdf")
        String fileName,

        @Schema(description = "Size of the resume file in bytes", example = "42501")
        Long fileSize
) {
}
