package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response with details about the uploaded resume")
public record ResumeResponse(
        @Schema(description = "Unique ID of the resume", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
        UUID id,

        @Schema(description = "Original filename of the uploaded resume", example = "cv_alex_spring.pdf")
        String fileName,

        @Schema(description = "Object key (path) in storage, use this to download the resume", example = "resume_7_1716851376.pdf")
        String objectKey,

        @Schema(description = "Size of the resume file in bytes", example = "42501")
        Long fileSize
) {
}
