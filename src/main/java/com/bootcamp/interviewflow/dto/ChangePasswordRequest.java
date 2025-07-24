package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request to change user password")
@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Size(min = 8, max = 255, message = "Current password must be between 8 and 255 characters")
    @Schema(description = "Current password", example = "OldPass123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 255, message = "New password must be between 8 and 255 characters")
    @Schema(description = "New password", example = "NewStrongPass456!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}