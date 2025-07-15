package com.bootcamp.interviewflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNoteDTO {
    @NotBlank(message = "Content is required")
    private String content;
}