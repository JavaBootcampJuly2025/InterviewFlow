package com.bootcamp.interviewflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Schema(description = "User response payload")
public class UserResponse {

    @Schema(description = "User's unique identifier", example = "42")
    private Long id;
    @Schema(description = "User's username", example = "John Doe")
    private String userName;
    @Schema(description = "User's email address", example = "johndoe@example.com")
    private String email;
    @CreationTimestamp
    @Schema(description = "Account creation timestamp", example = "2024-07-17T14:33:22")
    private LocalDateTime createdAt;
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    public UserResponse() {}

    public UserResponse(Long id, String userName, String email, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.createdAt = createdAt;
    }
    public UserResponse(Long id, String userName, String email, String accessToken) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.accessToken = accessToken;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}