package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response with details about the uploaded resume")
public record ResumeResponse(
        @Schema(description = "Unique ID of the resume", example = "101")
        Long id,

        @Schema(description = "Original filename of the uploaded resume", example = "cv_alex_spring.pdf")
        String fileName,

        @Schema(description = "URL to download or access the resume file", example = "https://interviewflow.com/api/resumes/101/download")
        String fileUrl,

        @Schema(description = "Size of the resume file in bytes", example = "42501")
        Long fileSize
) {
}
