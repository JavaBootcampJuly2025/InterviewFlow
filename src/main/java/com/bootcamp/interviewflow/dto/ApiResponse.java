package com.bootcamp.interviewflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response wrapper")
public class ApiResponse {
    @Schema(description = "Indicates whether the operation was successful", example = "false")
    private boolean success;
    @Schema(description = "Human-readable message about the result", example = "Operation failed")
    private String message;
    @Schema(description = "Returned data payload, can vary by operation")
    private Object data;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}